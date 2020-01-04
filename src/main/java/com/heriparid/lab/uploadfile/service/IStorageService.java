package com.heriparid.lab.uploadfile.service;

import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {
    boolean uploadFile(MultipartFile file);
}
