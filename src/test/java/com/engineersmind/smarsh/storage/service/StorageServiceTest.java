package com.engineersmind.smarsh.storage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class StorageServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private StorageService storageService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUploadFile() {
        try {
            // Mock the Amazon S3 client
            when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(null);

            // Create a sample MultipartFile for testing
            File sampleFile = new File("sample.txt");
            FileInputStream input = new FileInputStream(sampleFile);
            MultipartFile multipartFile = new MockMultipartFile("file", "sample.txt", "text/plain", IOUtils.toByteArray(input));

            // Call the method to be tested
            String result = storageService.uploadFile(multipartFile);

            // Verify that the S3 client was called with the correct parameters
            verify(s3Client).putObject(any(PutObjectRequest.class));

            // Assert the result
            assertEquals("File uploaded : sample.txt", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFileInfo() {
        try {
            // Mock the Amazon S3 client's getObjectMetadata method
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setSSEAlgorithm("AES256");
            objectMetadata.setContentLength(1024L);
            objectMetadata.setExpirationTime(new Date());
            objectMetadata.setOngoingRestore(false);
            objectMetadata.setContentType("text/plain");
            objectMetadata.setLastModified(new Date());

            when(s3Client.getObjectMetadata(anyString(), anyString())).thenReturn(objectMetadata);

            // Call the method to be tested
            Map<String, String> fileInfo = storageService.getFileInfo("sample.txt");

            // Verify that the S3 client's getObjectMetadata was called with the correct parameters
            verify(s3Client).getObjectMetadata(anyString(), anyString());

            // Assert the result
            assertEquals("sample.txt", fileInfo.get("File Name"));
            assertEquals("AES256", fileInfo.get("Server-Side Encryption"));
            assertEquals("1024", fileInfo.get("Size"));
            // Add more assertions for other metadata as needed
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
