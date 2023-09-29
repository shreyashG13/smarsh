package com.engineersmind.smarsh.storage.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.engineersmind.smarsh.storage.service.StorageService;

import com.engineersmind.smarsh.storage.service.ZipAndBatchService;
import com.engineersmind.smarsh.xml.service.SmarshXmlService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
@RestController
@RequestMapping("/pipeline")
public class PipelineBatchAndUploadController {

    @Autowired
    private ZipAndBatchService zipAndBatchService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private SmarshXmlService smarshXmlService;

   
    
    @Value("${directory.path}")
    private String dir;
    @Value("${directory.zippath}")
    private String zipDir;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("ddMMMMyyyy", Locale.ENGLISH);

    public String getFullDirectoryPath() {
        String formattedDate = LocalDate.now().format(DATE_FORMATTER).toLowerCase();

        Path fullPath = Paths.get(dir, formattedDate);

        // Ensure directory exists. If directory already exists, mkdirs has no effect.
        if (Files.notExists(fullPath)) {
            try {
                Files.createDirectories(fullPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + fullPath, e);
            }
        }

        return fullPath.toString();
    }
    public String getZippedFilesPath() {
        String formattedDate = LocalDate.now().format(DATE_FORMATTER).toLowerCase();

        Path fullPath = Paths.get(zipDir, formattedDate);
		return fullPath.toString();
        
    }
    

  
    public void deleteFilesFromDirectory(String directoryPath) {


    	File directory = new File(directoryPath);
    	File[] files = directory.listFiles();


    	if(directory.exists() && directory.isDirectory()) {
    		if (files != null) {
    			for (File file : files) {
    				if (file.isFile()) {
    					if (!file.delete()) {
    						System.err.println("Failed to delete file: " + file.getAbsolutePath());
    					}
    				}
    			}
    		}
    	}
    	else {
    		System.err.println("Directory does not exist or isn't a directory: " + directoryPath);
    	}
    }

	
    @PostMapping("/process-xmls")
    public ResponseEntity<String> processXmls(@RequestParam int count) {
        try {
            // Step 1: Generate XMLs
            smarshXmlService.getApiRequest();

            // Step 2: Zip the XMLs
            List<String> zippedFiles = zipAndBatchService.batchZipFilesInDirectory(getFullDirectoryPath(), count);
            
            // Step 3: Upload to S3
            zippedFiles.forEach(this::uploadFileToS3);
            
         // Step 4:After uploading to S3, delete the zipped files.
            String pathToDelete = getZippedFilesPath();
            System.out.println("Attempting to delete files from: " + pathToDelete);

            deleteFilesFromDirectory(getZippedFilesPath());
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error during XML processing, zipping, or uploading!");
        }
        
        return ResponseEntity.ok("XMLs generated, zipped, and uploaded successfully!");
    }
    @PostMapping("/zip-size")
    public ResponseEntity<String> zipBySize(@RequestParam long sizeMB) {
        try {
            // Step 1: Generate XMLs
            smarshXmlService.getApiRequest();

            // Step 2: Zip the XMLs based on size
            List<String> zippedFiles = zipAndBatchService.batchZipFilesInDirectoryBySize(dir, sizeMB);
            
            // Step 3: Upload to S3
            zippedFiles.forEach(this::uploadFileToS3);

        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error during XML processing, zipping, or uploading!");
        }

        return ResponseEntity.ok("XMLs generated, zipped by size, and uploaded successfully!");
    }

    @PostMapping("/zip-input-size")
    public ResponseEntity<String> zipByInputSize(@RequestParam long sizeMB) {
        try {
            // Step 1: Generate XMLs
            smarshXmlService.getApiRequest();

            // Step 2: Zip the XMLs based on input size
            List<String> zippedFiles = zipAndBatchService.batchZipFilesInDirectoryBySize2(dir, sizeMB);
            
            // Step 3: Upload to S3
            zippedFiles.forEach(this::uploadFileToS3);

        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error during XML processing, zipping, or uploading!");
        }

        return ResponseEntity.ok("XMLs generated, zipped by input size, and uploaded successfully!");
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