package com.engineersmind.smarsh.storage.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.springframework.stereotype.Service;

@Service
public class ZipAndBatchService {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
		String currentTime = LocalDateTime.now().format(formatter);
		// This will be the base directory where all zipped files will be stored.
		private static final String BASE_ZIPPED_DIRECTORY = "C:/work/zipped/";

		private String getZippedDirectoryPath() {
		    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMMMyyyy", Locale.ENGLISH);
		    String formattedDate = LocalDate.now().format(dateFormatter).toLowerCase();
		    
		    // Ensure that there's a directory separator at the end
		    String fullPath = BASE_ZIPPED_DIRECTORY.endsWith(File.separator) 
		                      ? BASE_ZIPPED_DIRECTORY + formattedDate 
		                      : BASE_ZIPPED_DIRECTORY + File.separator + formattedDate;

		    // Ensure directory exists
		    File directory = new File(fullPath);
		    if (!directory.exists()) {
		        directory.mkdirs();
		    }

		    // Return the full path with the directory separator at the end
		    return fullPath + File.separator;
		}

		public String zipFilesInDirectory(String directoryPath) {
		    String zipFileName = "zipped_files_" + currentTime  + ".zip";
		    String zipFilePath = getZippedDirectoryPath() + "/" + zipFileName;  // Added a separator

		    try (FileOutputStream fos = new FileOutputStream(zipFilePath);
		         ZipOutputStream zos = new ZipOutputStream(fos)) {

		        File directory = new File(directoryPath);
		        for (File file : directory.listFiles()) {
		            if (file.isFile()) {
		                ZipEntry zipEntry = new ZipEntry(file.getName());
		                zos.putNextEntry(zipEntry);

		                try (FileInputStream fis = new FileInputStream(file)) {
		                    byte[] buffer = new byte[1024];
		                    int length;
		                    while ((length = fis.read(buffer)) > 0) {
		                        zos.write(buffer, 0, length);
		                    }
		                }
		            }
		        }
		        return zipFilePath;
		    } catch (IOException e) {
		        e.printStackTrace();
		        return null;
		    }
		}
	    
	    public String zipFilesInDirectory2(String directoryPath) {
	        String zipFileName = "zipped_files_" + currentTime  + ".7z";
	        String zipFilePath = getZippedDirectoryPath() + zipFileName;

	        try (SevenZOutputFile sevenZOutputFile = new SevenZOutputFile(new File(zipFilePath))) {

	            File directory = new File(directoryPath);
	            if (!directory.exists() || !directory.isDirectory()) {
	                System.err.println("Invalid directory path: " + directoryPath);
	                return null;
	            }

	            for (File file : directory.listFiles()) {
	                if (file.isFile()) {
	                    SevenZArchiveEntry entry = sevenZOutputFile.createArchiveEntry(file, file.getName());
	                    sevenZOutputFile.putArchiveEntry(entry);

	                    try (FileInputStream fis = new FileInputStream(file)) {
	                        byte[] buffer = new byte[1024];
	                        int length;
	                        while ((length = fis.read(buffer)) > 0) {
	                            sevenZOutputFile.write(buffer, 0, length);
	                        }
	                    } catch (IOException ex) {
	                        System.err.println("Error while reading file: " + file.getAbsolutePath());
	                        ex.printStackTrace();
	                    }

	                    sevenZOutputFile.closeArchiveEntry();
	                }
	            }

	            return zipFilePath;
	        } catch (IOException e) {
	            System.err.println("Error while creating 7z file: " + zipFilePath);
	            e.printStackTrace();
	            return null;
	        }
	    }
	   //ZIPPING A FILES IN A BATCH WITH NUMBER OF FILES
	    public List<String> batchZipFilesInDirectory(String directoryPath, int batchSize) {
	        File directory = new File(directoryPath);
	        File[] files = directory.listFiles();

	        if (files == null) {
	            return Collections.emptyList();
	        }

	        List<String> zipFilePaths = new ArrayList<>();
	        int totalFiles = files.length;
	        int batchCount = (totalFiles + batchSize - 1) / batchSize; // Calculate number of batches

	        for (int batchIndex = 0; batchIndex < batchCount; batchIndex++) {
	            String batchZipFileName = "batch_" + batchIndex + "_" + currentTime  + ".zip";
	            
	            // file location where zipped files will be stored
	            String batchZipFilePath = getZippedDirectoryPath() + batchZipFileName;
	            zipFilePaths.add(batchZipFilePath);

	            try (FileOutputStream fos = new FileOutputStream(batchZipFilePath);
	                 ZipOutputStream zos = new ZipOutputStream(fos)) {

	                int startIndex = batchIndex * batchSize;
	                int endIndex = Math.min(startIndex + batchSize, totalFiles);

	                for (int i = startIndex; i < endIndex; i++) {
	                    File file = files[i];
	                    if (file.isFile()) {
	                        ZipEntry zipEntry = new ZipEntry(file.getName());
	                        zos.putNextEntry(zipEntry);

	                        try (FileInputStream fis = new FileInputStream(file)) {
	                            byte[] buffer = new byte[1024];
	                            int length;
	                            while ((length = fis.read(buffer)) > 0) {
	                                zos.write(buffer, 0, length);
	                            }
	                        }
	                    }
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	                return Collections.emptyList();
	            }
	        }

	        return zipFilePaths;
	    }
	    
	    
	    //ZIPPING A FILES IN A BATCH WITH FILE SIZE of created ZIP 
	    public List<String> batchZipFilesInDirectoryBySize(String directoryPath, long batchSizeMB) {
	        File directory = new File(directoryPath);
	        File[] files = directory.listFiles();

	        if (files == null) {
	            return Collections.emptyList();
	        }

	        List<String> zipFilePaths = new ArrayList<>();
	        int totalFiles = files.length;
	        int currentIndex = 0;

	        while (currentIndex < totalFiles) {
	            String batchZipFileName = "batch_" + currentIndex + currentTime  + ".zip";
	            String batchZipFilePath = getZippedDirectoryPath() + batchZipFileName;
	            zipFilePaths.add(batchZipFilePath);

	            try (FileOutputStream fos = new FileOutputStream(batchZipFilePath);
	                 ZipOutputStream zos = new ZipOutputStream(fos)) {

	                long currentBatchSize = 0;
	                while (currentIndex < totalFiles && currentBatchSize <= batchSizeMB * 1024 * 1024) {
	                    File file = files[currentIndex];
	                    if (file.isFile()) {
	                        ZipEntry zipEntry = new ZipEntry(file.getName());
	                        zos.putNextEntry(zipEntry);

	                        try (FileInputStream fis = new FileInputStream(file)) {
	                            byte[] buffer = new byte[1024];
	                            int length;
	                            while ((length = fis.read(buffer)) > 0) {
	                                zos.write(buffer, 0, length);
	                            }
	                        }

	                        currentBatchSize += file.length();
	                    }
	                    currentIndex++;
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	                return Collections.emptyList();
	            }
	        }

	        return zipFilePaths;
	    }

	    //ZIPPING A FILES IN A BATCH WITH FILE SIZE of input files
	    public List<String> batchZipFilesInDirectoryBySize2(String directoryPath, long batchSizeMB) {
	        File directory = new File(directoryPath);
	        File[] files = directory.listFiles();

	        if (files == null) {
	            return Collections.emptyList();
	        }

	        Arrays.sort(files, Comparator.comparingLong(File::length)); // Sort files by size

	        List<String> zipFilePaths = new ArrayList<>();
	        int currentIndex = 0;

	        while (currentIndex < files.length) {
	        	int n =1;
	            long currentBatchSize = 0;
	            List<File> batchFiles = new ArrayList<>();
	            
	            while (currentIndex < files.length && currentBatchSize + files[currentIndex].length() <= batchSizeMB * 1024 * 1024) {
	                batchFiles.add(files[currentIndex]);
	                currentBatchSize += files[currentIndex].length();
	                currentIndex++;
	            }
	            
	            if (!batchFiles.isEmpty()) {
	                String batchZipFileName = "batch_" +n+ currentTime  + ".zip";
	                String batchZipFilePath = getZippedDirectoryPath() + batchZipFileName;
	                zipFilePaths.add(batchZipFilePath);
	                n=n+1;
	                try (FileOutputStream fos = new FileOutputStream(batchZipFilePath);
	                     ZipOutputStream zos = new ZipOutputStream(fos)) {
	                     
	                    for (File file : batchFiles) {
	                        ZipEntry zipEntry = new ZipEntry(file.getName());
	                        zos.putNextEntry(zipEntry);

	                        try (FileInputStream fis = new FileInputStream(file)) {
	                            byte[] buffer = new byte[1024];
	                            int length;
	                            while ((length = fis.read(buffer)) > 0) {
	                                zos.write(buffer, 0, length);
	                            }
	                        }
	                    }
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    return Collections.emptyList();
	                }
	            }
	        }

	        return zipFilePaths;
	    }


}
