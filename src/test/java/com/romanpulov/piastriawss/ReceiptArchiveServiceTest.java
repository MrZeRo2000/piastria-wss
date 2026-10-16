package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.config.DBFileInfo;
import com.romanpulov.piastriawss.exception.BadRequestException;
import com.romanpulov.piastriawss.service.ReceiptArchiveService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ReceiptArchiveServiceTest {
    private static final DateTimeFormatter RECEIPT_FORMAT = DateTimeFormatter.ofPattern("yyMMdd_HHmm");

    // the target subfolder is TransliterationUtils.toLetterDigitCode(name)
    private static final String PAYMENT_OBJECT_NAME = "Киев 15/23";
    private static final String PAYMENT_OBJECT_FOLDER = "k1523";

    @TempDir
    Path tempDir;

    private Path sourceDir;
    private Path receiptDir;
    private Path targetDir;
    private ReceiptArchiveService service;

    @BeforeEach
    void setUp() throws IOException {
        Path tmp = Files.createDirectories(tempDir.resolve("tmp"));
        System.setProperty("java.io.tmpdir", tmp.toString());
        sourceDir = tmp.resolve("backup").resolve("receipt");

        // target folder is <dbFile>/../../receipt/<payment object folder>
        Path root = Files.createDirectories(tempDir.resolve("root"));
        Path dbDir = Files.createDirectories(root.resolve("db"));
        receiptDir = root.resolve("receipt");
        targetDir = receiptDir.resolve(PAYMENT_OBJECT_FOLDER);

        DBFileInfo dbFileInfo = new DBFileInfo(dbDir.resolve("piastria.db").toString(), "unused", "unused");
        service = new ReceiptArchiveService(dbFileInfo, mock(Logger.class));
        service.setRootFolder(tmp.toString());
    }

    private void createPdf(String name, Instant lastModified) throws IOException {
        Files.createDirectories(sourceDir);
        Path file = Files.writeString(sourceDir.resolve(name), "%PDF-1.4 test");
        Files.setLastModifiedTime(file, FileTime.from(lastModified));
    }

    private Path archivePathFor(Instant instant) {
        return targetDir.resolve("receipt" + RECEIPT_FORMAT.format(instant.atZone(ZoneId.systemDefault())) + ".zip");
    }

    @Test
    void archive_sourceFolderMissing_throws() throws IOException {
        Files.createDirectories(targetDir);

        BadRequestException e = assertThrows(BadRequestException.class, () -> service.archive(PAYMENT_OBJECT_NAME));

        assertTrue(e.getMessage().contains("Backup temporary directory"), e.getMessage());
    }

    @Test
    void archive_sourceIsFileNotDirectory_throws() throws IOException {
        Files.createDirectories(sourceDir.getParent());
        Files.writeString(sourceDir, "not a directory");
        Files.createDirectories(targetDir);

        BadRequestException e = assertThrows(BadRequestException.class, () -> service.archive(PAYMENT_OBJECT_NAME));

        assertTrue(e.getMessage().contains("Backup temporary directory"), e.getMessage());
    }

    @Test
    void archive_targetFolderMissing_throws() throws IOException {
        Files.createDirectories(sourceDir);

        BadRequestException e = assertThrows(BadRequestException.class, () -> service.archive(PAYMENT_OBJECT_NAME));

        assertTrue(e.getMessage().contains("Target temporary directory"), e.getMessage());
    }

    @Test
    void archive_receiptFolderExistsButPaymentObjectSubfolderMissing_throws() throws IOException {
        Files.createDirectories(sourceDir);
        Files.createDirectories(receiptDir);

        BadRequestException e = assertThrows(BadRequestException.class, () -> service.archive(PAYMENT_OBJECT_NAME));

        assertTrue(e.getMessage().contains("Target temporary directory"), e.getMessage());
        assertTrue(e.getMessage().contains(PAYMENT_OBJECT_FOLDER), e.getMessage());
    }

    @Test
    void archive_otherPaymentObject_usesItsOwnSubfolder() throws IOException {
        // only the folder of PAYMENT_OBJECT_NAME exists, so another object must not fall back to it
        Files.createDirectories(targetDir);
        createPdf("fresh.pdf", Instant.now());

        BadRequestException e = assertThrows(BadRequestException.class, () -> service.archive("Львів 7"));

        assertTrue(e.getMessage().contains("Target temporary directory"), e.getMessage());
        assertTrue(e.getMessage().contains("l7"), e.getMessage());
    }

    @Test
    void archive_oldFile_throws() throws IOException {
        Files.createDirectories(targetDir);
        createPdf("old.pdf", Instant.now().minus(Duration.ofHours(7)));

        BadRequestException e = assertThrows(BadRequestException.class, () -> service.archive(PAYMENT_OBJECT_NAME));

        assertTrue(e.getMessage().contains("File 'old.pdf' is old to archive"), e.getMessage());
    }

    @Test
    void archive_oneOldAmongFreshFiles_throws() throws IOException {
        Files.createDirectories(targetDir);
        createPdf("fresh.pdf", Instant.now());
        createPdf("old.pdf", Instant.now().minus(Duration.ofHours(7)));

        BadRequestException e = assertThrows(BadRequestException.class, () -> service.archive(PAYMENT_OBJECT_NAME));

        assertTrue(e.getMessage().contains("old.pdf"), e.getMessage());
    }

    @Test
    void archive_nonPdfFilesAreIgnoredByValidation() throws IOException {
        Files.createDirectories(targetDir);
        createPdf("fresh.pdf", Instant.now());
        Path txt = Files.writeString(sourceDir.resolve("old.txt"), "ignored");
        Files.setLastModifiedTime(txt, FileTime.from(Instant.now().minus(Duration.ofDays(3))));

        // fails later (no archive file), but not because of old.txt
        BadRequestException e = assertThrows(BadRequestException.class, () -> service.archive(PAYMENT_OBJECT_NAME));

        assertFalse(e.getMessage().contains("old.txt"), e.getMessage());
    }

    @Test
    void archive_freshFileButArchiveFileMissing_throws() throws IOException {
        Files.createDirectories(targetDir);
        createPdf("fresh.pdf", Instant.now());

        BadRequestException e = assertThrows(BadRequestException.class, () -> service.archive(PAYMENT_OBJECT_NAME));

        assertTrue(e.getMessage().matches(".*Target file 'receipt\\d{6}_\\d{4}\\.zip' does not exist.*"), e.getMessage());
    }

    @Test
    void archive_freshFileAndExistingArchiveFile_zipsFile() throws IOException {
        Files.createDirectories(targetDir);
        createPdf("fresh.pdf", Instant.now());
        // the name is minute-based; create the file for this and the next minute to avoid a boundary race
        Instant now = Instant.now();
        Path current = Files.createFile(archivePathFor(now));
        Path next = archivePathFor(now.plus(Duration.ofMinutes(1)));
        if (!Files.exists(next)) {
            Files.createFile(next);
        }

        Path written = assertDoesNotThrow(() -> service.archive(PAYMENT_OBJECT_NAME));

        assertTrue(written.equals(current) || written.equals(next), written.toString());
        assertEquals(targetDir, written.getParent());
        assertTrue(Files.size(written) > 0);
        try (ZipFile zip = new ZipFile(written.toFile())) {
            assertEquals(1, zip.size());
            assertNotNull(zip.getEntry("fresh.pdf"));
        }
    }

    @Test
    void list_generalTest() throws IOException, BadRequestException {
        Files.createDirectories(targetDir);
        createPdf("fresh1.pdf", Instant.now());
        createPdf("fresh2.pdf", Instant.now());

        var files = service.listFiles(PAYMENT_OBJECT_NAME);

        assertFalse(files.isEmpty());
        assertEquals("fresh1.pdf", files.get(0).file().getName());
        assertEquals("fresh2.pdf", files.get(1).file().getName());
    }
}
