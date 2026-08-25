package com.romanpulov.piastriawss;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class DBHelper {

    private static final Logger log = Logger.getLogger(DBHelper.class.getName());

    static void prepareTestDB() {
        log.info("Before all tests");

        String templateDBFileName = System.getProperty("user.dir") + File.separator + "db" + File.separator + "database" + File.separator + "piastria.db";
        String testDBFileName = System.getProperty("user.dir") + File.separator + "db" + File.separator + "database" + File.separator + "piastria-test.db";

        log.info("templateDBFileName:" + templateDBFileName);
        log.info("testDBPath:" + testDBFileName);

        Path templateDB = Paths.get(templateDBFileName);
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

        //copy new db
        try {
            Files.copy(templateDB, testDB);
        } catch (IOException e) {
            log.severe(String.format("Unable to copy test database file %s : %s", testDB, e.getMessage()));
        }
    }

}
