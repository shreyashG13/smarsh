package com.engineersmind.smarsh.storage.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.engineersmind.smarsh.storage.service.StorageService;

public class StorageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StorageService storageService;

    private StorageController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        controller = new StorageController();
        controller.service = storageService;

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testUploadFile() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());
        when(storageService.uploadFile(any(MultipartFile.class))).thenReturn("File uploaded successfully");

        // Act and Assert
        mockMvc.perform(multipart("/file/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("File uploaded successfully"));

        // Verify that the service method was called
        verify(storageService, times(1)).uploadFile(file);
    }

    @Test
    public void testDownloadFile() throws Exception {
        // Arrange
        String fileName = "test.txt";
        byte[] fileData = "Hello, World!".getBytes();
        ByteArrayResource resource = new ByteArrayResource(fileData);

        when(storageService.downloadFile(fileName)).thenReturn(fileData);

        // Act and Assert
        mockMvc.perform(get("/file/download/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(fileData))
                .andExpect(header().string("Content-disposition", "attachment; filename=\"" + fileName + "\""));

        // Verify that the service method was called
        verify(storageService, times(1)).downloadFile(fileName);
    }

    @Test
    public void testListFiles() throws Exception {
        // Arrange
        List<String> fileList = Arrays.asList("file1.txt", "file2.txt");
        when(storageService.listFiles()).thenReturn(fileList);

        // Act and Assert
        mockMvc.perform(get("/file/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("file1.txt"))
                .andExpect(jsonPath("$[1]").value("file2.txt"));

        // Verify that the service method was called
        verify(storageService, times(1)).listFiles();
    }

    @Test
    public void testGetFileInfo() throws Exception {
        // Arrange
        String fileName = "test.txt";
        Map<String, String> fileInfo = Map.of("name", fileName, "size", "10KB");
        when(storageService.getFileInfo(fileName)).thenReturn(fileInfo);

        // Act and Assert
        mockMvc.perform(get("/file/info/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(fileName))
                .andExpect(jsonPath("$.size").value("10KB"));

        // Verify that the service method was called
        verify(storageService, times(1)).getFileInfo(fileName);
    }

    @Test
    public void testDeleteFile() throws Exception {
        // Arrange
        String fileName = "test.txt";
        when(storageService.deleteFile(fileName)).thenReturn("File deleted successfully");

        // Act and Assert
        mockMvc.perform(delete("/file/delete/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(content().string("File deleted successfully"));

        // Verify that the service method was called
        verify(storageService, times(1)).deleteFile(fileName);
    }
}
