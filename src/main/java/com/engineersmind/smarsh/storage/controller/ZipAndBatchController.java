package com.engineersmind.smarsh.storage.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.engineersmind.smarsh.storage.service.ZipAndBatchService;


@RestController
@RequestMapping("/fileoperations")
public class ZipAndBatchController {
	
	@Autowired ZipAndBatchService service;
	
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
	    

}
