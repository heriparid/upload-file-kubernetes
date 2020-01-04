package com.heriparid.lab.uploadfile.controller;

import com.heriparid.lab.uploadfile.model.UploadFile;
import com.heriparid.lab.uploadfile.service.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@Slf4j
public class FileResourceController {

    @Autowired
    private IStorageService storageService;

    @PostMapping("/upload")
    public UploadFile uploadData(@RequestParam("file") MultipartFile file) {
        return storageService.storeFile(file);
    }

    @GetMapping("/resources-name/{fileName:.+}")
    public ResponseEntity<Resource> viewFileByFileName(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = storageService.loadFileAsResourceByName(fileName);
        return constructBodyResponse(resource, request);
    }

    @GetMapping("/resources-id/{fileId}")
    public ResponseEntity<Resource> viewFileByFileName(@PathVariable Long fileId, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = storageService.loadFileAsResourceById(fileId);
        return constructBodyResponse(resource, request);
    }

    private ResponseEntity<Resource> constructBodyResponse(Resource resource, HttpServletRequest request){
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        // inline == view | attachment == download
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
