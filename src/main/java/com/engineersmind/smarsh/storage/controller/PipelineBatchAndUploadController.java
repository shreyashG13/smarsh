package com.engineersmind.smarsh.storage.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.engineersmind.smarsh.storage.service.StorageService;
import com.engineersmind.smarsh.storage.service.ZipAndBatchService;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/pipeline")
public class PipelineBatchAndUploadController {

    @Autowired
    private ZipAndBatchService zipAndBatchService;

    @Autowired
    private StorageService storageService;

    @PostMapping("/zip-count")
    public ResponseEntity<String> zipByCount(@RequestParam String dir, @RequestParam int count) {
        List<String> zippedFiles = zipAndBatchService.batchZipFilesInDirectory(dir, count);
        zippedFiles.forEach(this::uploadFileToS3);
        return ResponseEntity.ok("Files zipped and uploaded successfully!");
    }

    @PostMapping("/zip-size")
    public ResponseEntity<String> zipBySize(@RequestParam String dir, @RequestParam long sizeMB) {
        List<String> zippedFiles = zipAndBatchService.batchZipFilesInDirectoryBySize(dir, sizeMB);
        zippedFiles.forEach(this::uploadFileToS3);
        return ResponseEntity.ok("Files zipped and uploaded successfully!");
    }

    @PostMapping("/zip-input-size")
    public ResponseEntity<String> zipByInputSize(@RequestParam String dir, @RequestParam long sizeMB) {
        List<String> zippedFiles = zipAndBatchService.batchZipFilesInDirectoryBySize2(dir, sizeMB);
        zippedFiles.forEach(this::uploadFileToS3);
        return ResponseEntity.ok("Files zipped and uploaded successfully!");
    }

    private void uploadFileToS3(String filePath) {
        try {
            File file = new File(filePath);
            storageService.uploadFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}