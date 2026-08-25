package com.romanpulov.piastriawss.service;

import com.romanpulov.jutilscore.storage.BackupUtils;
import com.romanpulov.piastriawss.config.DBFileInfo;
import com.romanpulov.piastriawss.dto.BackupDatabaseInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BackupService {
    private final DBFileInfo dbFileInfo;

    @Autowired
    public BackupService(DBFileInfo dbFileInfo) {
        this.dbFileInfo = dbFileInfo;
    }

    public String backupDatabase() {
        return this.dbFileInfo.getDBBackupPath() + File.separator + BackupUtils.createRollingLocalBackup(
                dbFileInfo.getDBFileName(),
                dbFileInfo.getDBBackupPath(),
                dbFileInfo.getBackupFileName()
        ) + ".zip";
    }

    public BackupDatabaseInfoDTO getBackupDatabaseInfo() {
        int backupFileCount = 0;
        LocalDateTime lastBackupDateTime = null;

        File[] backupFiles = BackupUtils.getBackupFiles(dbFileInfo.getDBBackupPath());
        if ((backupFiles != null) && ((backupFileCount = backupFiles.length) > 0)) {
            List<File> fileList = Arrays.asList(backupFiles);
            Optional<Long> lastModified = fileList.stream().map(File::lastModified).max(Long::compareTo);

            if (lastModified.isPresent()) {
                Date lastModifiedDate = new Date(lastModified.get());
                lastBackupDateTime = lastModifiedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
        }

        return new BackupDatabaseInfoDTO(lastBackupDateTime, backupFileCount);
    }
}
