package com.engineersmind.smarsh.storage.service;

import com.engineersmind.smarsh.storage.controller.ZipAndBatchController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class ZipAndBatchServiceTest {

    @InjectMocks
    private ZipAndBatchController controller;

    @Mock
    private ZipAndBatchService service;

    @BeforeEach
    public void setUp() {

    }


    @Test
    public void testZipDirectory() {
        // Mock the behavior of the service's zipFilesInDirectory method
        Mockito.when(service.zipFilesInDirectory(eq("testDir"))).thenReturn("testDir.zip");

        // Call the controller method
        ResponseEntity<String> responseEntity = controller.zipDirectory("testDir");

        // Verify that the status code is OK (200)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Files zipped and saved as: testDir.zip", responseEntity.getBody());
    }



    @Test
    public void testBatchZipDirectoryBySize2() {
        // Mock the behavior of the service's batchZipFilesInDirectoryBySize2 method
        List<String> zipFilePaths = new ArrayList<>();
        zipFilePaths.add("file1.zip");
        zipFilePaths.add("file2.zip");
        Mockito.when(service.batchZipFilesInDirectoryBySize2(eq("testDir"), eq(1024L))).thenReturn(zipFilePaths);

        // Call the controller method
        ResponseEntity<List<String>> responseEntity = controller.batchZipDirectoryBySize2("testDir", 1024L);

        // Verify that the status code is OK (200)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(zipFilePaths, responseEntity.getBody());
    }
}
