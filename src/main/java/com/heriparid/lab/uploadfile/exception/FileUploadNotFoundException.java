package com.heriparid.lab.uploadfile.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileUploadNotFoundException extends RuntimeException {

    public FileUploadNotFoundException(String message) {
        super(message);
    }

    public FileUploadNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
