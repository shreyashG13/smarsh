package com.engineersmind.smarsh.storage.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.engineersmind.smarsh.storage.service.ZipAndBatchService;

public class ZipAndBatchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ZipAndBatchService zipAndBatchService;

    private ZipAndBatchController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        controller = new ZipAndBatchController();
        controller.service = zipAndBatchService;

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testZipDirectory() throws Exception {
        // Arrange
        String directoryPath = "/path/to/directory";
        when(zipAndBatchService.zipFilesInDirectory(directoryPath)).thenReturn("file1.zip");

        // Act and Assert
        mockMvc.perform(post("/fileoperations/zip")
                        .param("directoryPath", directoryPath))
                .andExpect(status().isOk())
                .andExpect(content().string("Files zipped and saved as: file1.zip"));

        // Verify that the service method was called
        verify(zipAndBatchService, times(1)).zipFilesInDirectory(directoryPath);
    }

    @Test
    public void testBatchZipDirectoryByCount() throws Exception {
        // Arrange
        String directoryPath = "/path/to/directory";
        int numFilesPerBatch = 5;
        List<String> zipFilePaths = Arrays.asList("file1.zip", "file2.zip");

        when(zipAndBatchService.batchZipFilesInDirectory(directoryPath, numFilesPerBatch)).thenReturn(zipFilePaths);

        // Act and Assert
        mockMvc.perform(post("/fileoperations/batch-zip-by-count")
                        .param("directoryPath", directoryPath)
                        .param("numFilesPerBatch", String.valueOf(numFilesPerBatch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("file1.zip"))
                .andExpect(jsonPath("$[1]").value("file2.zip"));

        // Verify that the service method was called
        verify(zipAndBatchService, times(1)).batchZipFilesInDirectory(directoryPath, numFilesPerBatch);
    }

    @Test
    public void testBatchZipDirectoryBySize() throws Exception {
        // Arrange
        String directoryPath = "/path/to/directory";
        long batchSizeMB = 10;
        List<String> zipFilePaths = Arrays.asList("file1.zip", "file2.zip");

        when(zipAndBatchService.batchZipFilesInDirectoryBySize(directoryPath, batchSizeMB)).thenReturn(zipFilePaths);

        // Act and Assert
        mockMvc.perform(post("/fileoperations/batch-zip-by-size")
                        .param("directoryPath", directoryPath)
                        .param("batchSizeMB", String.valueOf(batchSizeMB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("file1.zip"))
                .andExpect(jsonPath("$[1]").value("file2.zip"));

        // Verify that the service method was called
        verify(zipAndBatchService, times(1)).batchZipFilesInDirectoryBySize(directoryPath, batchSizeMB);
    }

    @Test
    public void testBatchZipDirectoryBySize2() throws Exception {
        // Arrange
        String directoryPath = "/path/to/directory";
        long batchSizeMB = 10;
        List<String> zipFilePaths = Arrays.asList("file1.zip", "file2.zip");

        when(zipAndBatchService.batchZipFilesInDirectoryBySize2(directoryPath, batchSizeMB)).thenReturn(zipFilePaths);

        // Act and Assert
        mockMvc.perform(post("/fileoperations/batch-zip-by-input-size")
                        .param("directoryPath", directoryPath)
                        .param("batchSizeMB", String.valueOf(batchSizeMB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("file1.zip"))
                .andExpect(jsonPath("$[1]").value("file2.zip"));

        // Verify that the service method was called
        verify(zipAndBatchService, times(1)).batchZipFilesInDirectoryBySize2(directoryPath, batchSizeMB);
    }
}
