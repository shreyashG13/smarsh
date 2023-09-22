package com.engineersmind.smarsh.storage.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.engineersmind.smarsh.storage.service.StorageService;



@RestController
@RequestMapping("/file")
public class StorageController {

    @Autowired
    private StorageService service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file) {
        return new ResponseEntity<>(service.uploadFile(file), HttpStatus.OK);
    }
    
    
    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        byte[] data = service.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {
        List<String> fileList = service.listFiles();
        return ResponseEntity.ok(fileList);
    }
    @GetMapping("/info/{fileName}")
    public ResponseEntity<Map<String, String>> getFileInfo(@PathVariable String fileName) {
        Map<String, String> fileInfo = service.getFileInfo(fileName);
        
        if (fileInfo != null) {
            return ResponseEntity.ok(fileInfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        return new ResponseEntity<>(service.deleteFile(fileName), HttpStatus.OK);
    }
    
    
    /*
    @PostMapping("/zip")
    public ResponseEntity<String> zipDirectory(@RequestParam(value = "directoryPath") String directoryPath) {
        String zipFileName = service.zipFilesInDirectory(directoryPath);

        if (zipFileName != null) {
            return ResponseEntity.ok("Files zipped and saved as: " + zipFileName);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to zip files.");
        }
    }
    @PostMapping("/zip2")
    public ResponseEntity<String> zipDirectory2(@RequestParam(value = "directoryPath") String directoryPath) {
        String zipFileName = service.zipFilesInDirectory2(directoryPath);

        if (zipFileName != null) {
            return ResponseEntity.ok("Files zipped and saved as: " + zipFileName);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to zip files.");
        }
    }
    //BATCH ZIPPING WITH NUMBER OF FILES 
    @PostMapping("/batch-zip-by-count")
    public ResponseEntity<List<String>> batchZipDirectoryByCount(@RequestParam(value = "directoryPath") String directoryPath,
                                                                 @RequestParam(value = "numFilesPerBatch") int numFilesPerBatch) {
        List<String> zipFilePaths = service.batchZipFilesInDirectory(directoryPath, numFilesPerBatch);

        if (!zipFilePaths.isEmpty()) {
            return ResponseEntity.ok(zipFilePaths);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    //BATCH ZIPPING WITH size of zip  
    @PostMapping("/batch-zip-by-size")
    public ResponseEntity<List<String>> batchZipDirectoryBySize(@RequestParam(value = "directoryPath") String directoryPath,
                                                                @RequestParam(value = "batchSizeMB") long batchSizeMB) {
        List<String> zipFilePaths = service.batchZipFilesInDirectoryBySize(directoryPath, batchSizeMB);

        if (!zipFilePaths.isEmpty()) {
            return ResponseEntity.ok(zipFilePaths);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    //BATCH ZIPPING WITH size of input files
    @PostMapping("/batch-zip-by-input-size")
    public ResponseEntity<List<String>> batchZipDirectoryBySize2(@RequestParam(value = "directoryPath") String directoryPath,
                                                                @RequestParam(value = "batchSizeMB") long batchSizeMB) {
        List<String> zipFilePaths = service.batchZipFilesInDirectoryBySize2(directoryPath, batchSizeMB);

        if (!zipFilePaths.isEmpty()) {
            return ResponseEntity.ok(zipFilePaths);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
 */   
}