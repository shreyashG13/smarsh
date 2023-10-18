package com.engineersmind.smarsh.storage.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZipAndBatchServiceTest {

    private ZipAndBatchService zipAndBatchService;

    @BeforeEach
    public void setUp() {
        zipAndBatchService = new ZipAndBatchService();
    }

    @Test
    public void testZipFilesInDirectory() throws IOException {
        // Create a temporary directory for testing
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "testDir");
        tempDir.toFile().mkdir();

        // Create sample files in the temporary directory
        File file1 = new File(tempDir.toFile(), "file1.txt");
        File file2 = new File(tempDir.toFile(), "file2.txt");
        file1.createNewFile();
        file2.createNewFile();

        // Zip the files in the temporary directory
        String zipFilePath = zipAndBatchService.zipFilesInDirectory(tempDir.toString());

        // Check if the zip file was created
        File zipFile = new File(zipFilePath);
        assertTrue(zipFile.exists());


    }
}
