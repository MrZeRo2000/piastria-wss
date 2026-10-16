package com.romanpulov.piastriawss.service;

import com.romanpulov.jutilscore.io.FileUtils;
import com.romanpulov.jutilscore.io.ZipFileUtils;
import com.romanpulov.piastriawss.config.DBFileInfo;
import com.romanpulov.piastriawss.exception.BadRequestException;
import com.romanpulov.piastriawss.utils.TransliterationUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ReceiptArchiveService {
    private static final String ARCHIVE_BACKUP_FOLDER_NAME = "backup";
    private static final String ARCHIVE_FOLDER_NAME = "receipt";
    private static final String ARCHIVE_FILE_NAME_FORMAT = "receipt%s.zip";
    private static final DateTimeFormatter RECEIPT_FORMAT = DateTimeFormatter.ofPattern("yyMMdd_HHmm");

    private final DBFileInfo dbFileInfo;
    private final Logger logger;

    public ReceiptArchiveService(DBFileInfo dbFileInfo, Logger logger) {
        this.dbFileInfo = dbFileInfo;
        this.logger = logger;
    }

    private String getArchiveFileName() {
        return String.format(ARCHIVE_FILE_NAME_FORMAT, RECEIPT_FORMAT.format(Instant.now().atZone(ZoneId.systemDefault())));
    }

    private record Settings(String sourceFolder, String targetFolder) {}

    private Settings getSettings(String paymentObjectName) throws BadRequestException {
        Path sourcePath = Paths.get(System.getProperty("java.io.tmpdir"), ARCHIVE_BACKUP_FOLDER_NAME, ARCHIVE_FOLDER_NAME);
        if (!Files.exists(sourcePath) || !Files.isDirectory(sourcePath)) {
            throw new BadRequestException(String.format("Backup temporary directory '%s' does not exist", sourcePath));
        }

        String targetSubFolderName = TransliterationUtils.toLetterDigitCode(paymentObjectName);
        Path targetPath = Paths.get(Paths.get(this.dbFileInfo.getDBFileName()).getParent().getParent().toString(), ARCHIVE_FOLDER_NAME, targetSubFolderName);
        if (!Files.exists(targetPath) || !Files.isDirectory(targetPath)) {
            throw new BadRequestException(String.format("Target temporary directory '%s' does not exist", targetPath));
        }

        return new Settings(sourcePath.toString(), targetPath.toString());
    }

    public record FileInfo(File file, Instant modifiedTime) {}

    public List<FileInfo> listFiles(String paymentObjectName) throws BadRequestException {
        List<FileInfo> result = new ArrayList<>();

        Settings settings = getSettings(paymentObjectName);
        final Collection<File> files = FileUtils.listFiles(settings.sourceFolder, "*.pdf");
        for (File file : files) {
            try {
                BasicFileAttributes attr = Files.readAttributes(Path.of(file.getAbsolutePath()), BasicFileAttributes.class);
                FileTime fileTime = Optional.ofNullable(attr.lastModifiedTime()).orElse(attr.creationTime());
                result.add(new FileInfo(file, fileTime.toInstant()));
            } catch (IOException e) {
                throw new BadRequestException(String.format("Error reading file '%s'", file.getAbsolutePath()));
            }
        }

        return result;
    }

    private void validate(List<FileInfo> fileInfos) throws BadRequestException {
        Instant current = Instant.now().minus(Duration.ofHours(6));
        for (FileInfo fileInfo : fileInfos) {
            if (!fileInfo.modifiedTime.isAfter(current)) {
                throw new BadRequestException(String.format("File '%s' is old to archive", fileInfo.file.getName()));
            }
        }
    }

    private Path createArchive(Settings settings, List<FileInfo> fileInfos) throws BadRequestException {
        Path archiveFilePath = Paths.get(settings.targetFolder, this.getArchiveFileName());
        logger.debug("Archive file: {}", archiveFilePath);

        if (!Files.exists(archiveFilePath)) {
            throw new BadRequestException(String.format("Target file '%s' does not exist", archiveFilePath.getFileName()));
        }

        if (!ZipFileUtils.zipFiles(archiveFilePath.toString(), fileInfos.stream().map(v -> v.file).toList())) {
            throw new BadRequestException(String.format("Error creating archive file '%s'", archiveFilePath.getFileName()));
        }

        return archiveFilePath;
    }

    public Path archive(String paymentObjectName) throws BadRequestException {
        Settings settings = this.getSettings(paymentObjectName);
        logger.debug("Archive settings: {}", settings);

        List<FileInfo> fileInfos = listFiles(paymentObjectName);
        this.validate(fileInfos);

        return this.createArchive(settings, fileInfos);
    }
}
