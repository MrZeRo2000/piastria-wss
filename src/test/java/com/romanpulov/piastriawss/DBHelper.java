package com.romanpulov.piastriawss;

import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DBHelper {

    private static final Logger log = Logger.getLogger(DBHelper.class.getName());

    static void prepareTestDB() {
        log.info("Before all tests");

        String schemaFileName = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "db" + File.separator + "schema.sql";
        String testDBFileName = System.getProperty("user.dir") + File.separator + "db" + File.separator + "database" + File.separator + "piastria-test.db";

        log.info("schemaFileName:" + schemaFileName);
        log.info("testDBPath:" + testDBFileName);

        Path testDB = Paths.get(testDBFileName);

        //delete test
        if (Files.exists(testDB)) {
            log.info(String.format("Deleting database file: %s", testDB));
            try {
                Files.delete(testDB);
            } catch (IOException e) {
                log.severe(String.format("Unable to delete test database file %s : %s", testDB, e.getMessage()));
            }
        }

        //build fresh db from schema script
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + testDBFileName)) {
            ScriptUtils.executeSqlScript(connection, new FileSystemResource(schemaFileName));
        } catch (SQLException e) {
            log.severe(String.format("Unable to build test database file %s : %s", testDB, e.getMessage()));
        }
    }

}
