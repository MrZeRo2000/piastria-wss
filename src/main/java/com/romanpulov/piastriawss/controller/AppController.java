package com.romanpulov.piastriawss.controller;

import com.romanpulov.piastriawss.dto.BackupDatabaseInfoDTO;
import com.romanpulov.piastriawss.dto.MessageDTO;
import com.romanpulov.piastriawss.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    private final BackupService backupService;

    @Autowired
    public AppController(BackupService backupService) {
        this.backupService = backupService;
    }

    @GetMapping("/app:backup_database_info")
    ResponseEntity<BackupDatabaseInfoDTO> getBackupDatabaseInfo() {
        return ResponseEntity.ok(backupService.getBackupDatabaseInfo());
    }

    @PostMapping("/app:backup_database")
    ResponseEntity<MessageDTO> backupDatabase() {
        String backupResult = backupService.backupDatabase();
        if (backupResult == null) {
            throw new RuntimeException("Error creating database backup");
        } else {
            return ResponseEntity.ok(new MessageDTO(backupResult));
        }
    }
}
