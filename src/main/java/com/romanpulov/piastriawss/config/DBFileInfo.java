package com.romanpulov.piastriawss.config;

public class DBFileInfo {

    private final String dbFileName;
    private final String dbBackupPath;
    private final String backupFileName;

    public DBFileInfo(String dbFileName, String dbBackupPath, String backupFileName) {
        this.dbFileName = dbFileName;
        this.dbBackupPath = dbBackupPath;
        this.backupFileName = backupFileName;
    }

    public String getDBFileName() {
        return dbFileName;
    }

    public String getDBBackupPath() {
        return dbBackupPath;
    }

    public String getBackupFileName() {
        return backupFileName;
    }

    @Override
    public String toString() {
        return "DBFileInfo{" +
                "dbFileName='" + dbFileName + '\'' +
                ", dbBackupPath='" + dbBackupPath + '\'' +
                ", backupFileName='" + backupFileName + '\'' +
                '}';
    }
}

