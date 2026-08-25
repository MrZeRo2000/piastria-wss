package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.dto.BackupDatabaseInfoDTO;
import com.romanpulov.piastriawss.service.BackupService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceBackupTest {

    @Autowired
    BackupService backupService;

    @Test
    void testBackupService() {
        Assertions.assertNotNull(this.backupService);

        String backupResult = this.backupService.backupDatabase();
        Assertions.assertNotNull(backupResult);

        Path backupFilePath = Paths.get(backupResult);
        Assertions.assertTrue(Files.exists(backupFilePath));

        BackupDatabaseInfoDTO backupDatabaseInfoDTO = this.backupService.getBackupDatabaseInfo();
        Assertions.assertNotNull(backupDatabaseInfoDTO);

        Assertions.assertTrue(ChronoUnit.SECONDS.between(LocalDateTime.now(), backupDatabaseInfoDTO.getLastBackupDateTime()) < 2);
    }
}
