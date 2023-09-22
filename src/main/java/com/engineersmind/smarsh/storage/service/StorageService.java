package com.engineersmind.smarsh.storage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;





@Service
@Slf4j
public class StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;
/*
    public String uploadFile(MultipartFile file) {
        try {
            File fileObj = convertMultiPartFileToFile(file);
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
            fileObj.delete();
            return "File uploaded : " + fileName;
        } catch (MaxUploadSizeExceededException ex) {
            return "File size exceeds the allowed limit.";
        }
    }
  */ 
    
    //FOR NORMAL FILE UPLOAD
    public String uploadFile(MultipartFile file) {
        try {
            File fileObj = convertMultiPartFileToFile(file);
            
            // 1. Get the current date and format it to the desired structure.
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMMMyyyy", Locale.ENGLISH);
            String formattedDate = currentDate.format(formatter).toLowerCase(); // e.g., "13sept2023"

            // 2. Prepend this formatted date string to the file name.
            String fileName = formattedDate + "/" +  file.getOriginalFilename();
            
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
            fileObj.delete();
            return "File uploaded : " + fileName;
        } catch (MaxUploadSizeExceededException ex) {
            return "File size exceeds the allowed limit.";
        }
    }
  //FOR  FILE UPLOAD FOR MULTIPKLE ILES THROGH PIPLINE
    public String uploadFile(File file) {
        try {
            // 1. Get the current date and format it to the desired structure.
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMMMyyyy", Locale.ENGLISH);
            String formattedDate = currentDate.format(formatter).toLowerCase(); // e.g., "13sept2023"

            // 2. Prepend this formatted date string to the file name.
            String fileName =  formattedDate + "/" +file.getName();

            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
            return "File uploaded : " + fileName;
        } catch (Exception e) {
          
            return "Failed to upload file.";
        }
    }


    public Map<String, String> getFileInfo(String fileName) {
        Map<String, String> fileInfo = new HashMap<>();
        
        ObjectMetadata objectMetadata = s3Client.getObjectMetadata(bucketName, fileName);
        fileInfo.put("File Name", fileName);
        fileInfo.put("Server-Side Encryption", objectMetadata.getSSEAlgorithm());
        fileInfo.put("Size", String.valueOf(objectMetadata.getContentLength()));
        fileInfo.put("Expiration Date", String.valueOf(objectMetadata.getExpirationTime()));
        fileInfo.put("ACL", objectMetadata.getOngoingRestore() != null ? objectMetadata.getOngoingRestore().toString() : "N/A"); // Convert Boolean to String
        fileInfo.put("File Type", objectMetadata.getContentType());
        fileInfo.put("Upload Time", String.valueOf(objectMetadata.getLastModified()));
     
        return fileInfo;
    }
    public List<String> listFiles() {
        List<String> fileList = new ArrayList<>();
        ObjectListing objectListing = s3Client.listObjects(bucketName);
        
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            fileList.add(objectSummary.getKey());
        }
        
        return fileList;
    }

    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
        return fileName + " removed ...";
    }


    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            
        }
        return convertedFile;
    }
    /*
    public String zipFilesInDirectory(String directoryPath) {
        String zipFileName = "zipped_files_" + System.currentTimeMillis() + ".zip";
        String zipFilePath = "C:/work/zipped/" + zipFileName;

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
        String zipFileName = "zipped_files_" + System.currentTimeMillis() + ".7z";
        String zipFilePath = "C:/work/zipped/" + zipFileName;

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
            String batchZipFileName = "batch_" + batchIndex + "_" + System.currentTimeMillis() + ".zip";
            String batchZipFilePath = "C:/work/zipped/" + batchZipFileName;
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
            String batchZipFileName = "batch_" + currentIndex + System.currentTimeMillis() + ".zip";
            String batchZipFilePath = "C:/work/zipped/" + batchZipFileName;
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
                String batchZipFileName = "batch_" +n+ System.currentTimeMillis() + ".zip";
                String batchZipFilePath = "C:/work/zipped/" + batchZipFileName;
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
    */

}