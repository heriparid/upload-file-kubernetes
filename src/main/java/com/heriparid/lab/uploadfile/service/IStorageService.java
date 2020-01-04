package com.heriparid.lab.uploadfile.service;

import com.heriparid.lab.uploadfile.model.UploadFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {
    UploadFile storeFile(MultipartFile file);
    Resource loadFileAsResourceByName(String fileName);
    Resource loadFileAsResourceById(Long fileId);
}
