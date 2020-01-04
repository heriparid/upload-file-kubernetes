package com.heriparid.lab.uploadfile.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class StorageService implements IStorageService {

    private final String path;

    @Autowired
    public StorageService(
            @Value("${upload.path}") String path){

        this.path = path;
    }

    @PostConstruct
    public void init() throws IOException {
        if(!Files.exists(Paths.get(path))) {
            Path createdPath = Files.createDirectory(Paths.get(path));
            log.info("Directory: {} was created", createdPath.toAbsolutePath());
        }
    }

    @Override
    public boolean uploadFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("You must select the a file for uploading");
        }

        boolean result = false;

        try {
            InputStream inputStream = file.getInputStream();
            String originalName = file.getOriginalFilename();
            String contentType = file.getContentType();
            long size = file.getSize();

            Files.copy(
                    inputStream,
                    Paths.get(path + "/" + originalName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            log.info("File: {} - {} Size: {} was uploaded to server", originalName, contentType, size);
            result = true;
        }catch (IOException e){
            log.error("Failed to store file {}", file.getOriginalFilename());
            // throw RuntiimeExcetion??
            e.printStackTrace();
        }

        return result;
    }
}
