package com.romanpulov.piastriawss.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Single shared {@link OkHttpClient} for the whole application.
 * <p>
 * The client owns a connection pool and a dispatcher thread pool, both created lazily by
 * this web application. Deployed as a war into Tomcat those threads belong to the webapp
 * class loader, so they have to be torn down explicitly on undeploy - otherwise Tomcat
 * reports "web application appears to have started a thread but failed to stop it" and
 * leaks the class loader until the threads idle out.
 * <p>
 * OkHttp 4.x has no {@code close()}; the teardown below is the documented equivalent.
 * On OkHttp 5 it can be replaced by a plain {@code okHttpClient.close()}.
 */
@Configuration
public class OkHttpConfig {
    private static final Logger logger = LoggerFactory.getLogger(OkHttpConfig.class);

    private static final int MAX_IDLE_CONNECTIONS = 10;
    private static final Duration KEEP_ALIVE = Duration.ofMinutes(5);

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration WRITE_TIMEOUT = Duration.ofSeconds(30);

    /**
     * Hard cap on the complete call, including connect, all redirects and retries.
     * The connect/read/write timeouts are per hop and reset on every received byte,
     * so this is the only value that bounds the total time a caller can block.
     */
    private static final Duration CALL_TIMEOUT = Duration.ofSeconds(60);

    /** How long shutdown waits for in-flight calls before the dispatcher is forcibly terminated. */
    private static final Duration SHUTDOWN_TIMEOUT = Duration.ofSeconds(10);

    /** Additional wait after a forced dispatcher shutdown. */
    private static final Duration SHUTDOWN_FORCE_TIMEOUT = Duration.ofSeconds(5);

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(
                        MAX_IDLE_CONNECTIONS,
                        KEEP_ALIVE.toMillis(),
                        TimeUnit.MILLISECONDS))
                .connectTimeout(CONNECT_TIMEOUT)
                .readTimeout(READ_TIMEOUT)
                .writeTimeout(WRITE_TIMEOUT)
                .callTimeout(CALL_TIMEOUT)
                .build();
    }

    /**
     * Shuts the client down on context close. Registered as a separate bean so the teardown
     * always runs against the {@code OkHttpClient} actually present in the context, and so
     * that Spring destroys it before the client bean itself.
     */
    @Bean
    public DisposableBean okHttpClientShutdown(OkHttpClient okHttpClient) {
        return () -> shutdown(okHttpClient);
    }

    private static void shutdown(OkHttpClient okHttpClient) {
        logger.info("Shutting down OkHttpClient...");

        ExecutorService executorService = okHttpClient.dispatcher().executorService();
        executorService.shutdown();

        try {
            if (!awaitTermination(executorService, SHUTDOWN_TIMEOUT)) {
                logger.warn("OkHttp dispatcher did not terminate within {}, forcing shutdown...", SHUTDOWN_TIMEOUT);
                executorService.shutdownNow();

                if (!awaitTermination(executorService, SHUTDOWN_FORCE_TIMEOUT)) {
                    logger.error("OkHttp dispatcher did not terminate even after shutdownNow()");
                }
            }
        } catch (InterruptedException e) {
            logger.warn("Interrupted while waiting for OkHttpClient shutdown, forcing shutdown...", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            // Runs on every path, including the interrupted one: releases the pooled sockets
            // and lets the pool cleanup threads exit.
            okHttpClient.connectionPool().evictAll();
            logger.info("OkHttpClient shutdown completed");
        }
    }

    private static boolean awaitTermination(ExecutorService executorService, Duration timeout)
            throws InterruptedException {
        return executorService.awaitTermination(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }
}
