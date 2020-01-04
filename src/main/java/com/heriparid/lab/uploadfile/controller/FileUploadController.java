package com.heriparid.lab.uploadfile.controller;

import com.heriparid.lab.uploadfile.service.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class FileUploadController {

    @Autowired
    private IStorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadData(@RequestParam("file") MultipartFile file) {
        storageService.uploadFile(file);
        return new ResponseEntity<String>("upload-file", HttpStatus.OK);
    }
}
