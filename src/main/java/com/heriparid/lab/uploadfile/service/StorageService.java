package com.heriparid.lab.uploadfile.service;

import com.heriparid.lab.uploadfile.component.SequenceGenerator;
import com.heriparid.lab.uploadfile.exception.FileStorageException;
import com.heriparid.lab.uploadfile.exception.FileUploadNotFoundException;
import com.heriparid.lab.uploadfile.model.IUploadFileRepository;
import com.heriparid.lab.uploadfile.model.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class StorageService implements IStorageService {

    private final Path fileStorageLocation;
    private final SequenceGenerator sequenceGenerator;
    private final IUploadFileRepository uploadFileRepository;

    @Autowired
    public StorageService(
            @Value("${upload.path}") String path,
            SequenceGenerator sequenceGenerator,
            IUploadFileRepository uploadFileRepository){

        this.fileStorageLocation = Paths.get(path).toAbsolutePath().normalize();
        this.sequenceGenerator = sequenceGenerator;
        this.uploadFileRepository = uploadFileRepository;

        try {
            if(!Files.exists(fileStorageLocation)) {
                Path createdPath = Files.createDirectories(this.fileStorageLocation);
                log.info("Directory: {} was created", createdPath.toAbsolutePath().normalize());
            }

        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public UploadFile storeFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new FileStorageException("You must select the a file for uploading");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        if(originalName.contains("..")){
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + originalName);
        }

        String contentType = file.getContentType();
        long size = file.getSize();

        try {
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = fileStorageLocation.resolve(originalName);
            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            log.info("File: {} - {} Size: {} was uploaded to server", originalName, contentType, size);

            // Put detail in DB
            Long id = sequenceGenerator.nextId();
            UploadFile uploadFile = new UploadFile(id);
            uploadFile.setName(originalName);
            uploadFile.setUri("/resources-id/" + id);
            uploadFile = uploadFileRepository.save(uploadFile);

            return uploadFile;
        }catch (IOException e){
            log.error("Failed to store file {}", originalName);
            throw new FileStorageException("Could not store file" + originalName + ". Please try again!", e);
        }

    }

    @Override
    public Resource loadFileAsResourceByName(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                log.info("Found a file-name {}", fileName);
                return resource;
            } else {
                log.warn("File not found {}", fileName);
                throw new FileUploadNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException e) {
            log.error("File not found {}", fileName);
            throw new FileUploadNotFoundException("File not found " + fileName, e);
        }
    }

    @Override
    public Resource loadFileAsResourceById(Long fileId) {
        Optional<UploadFile> fileUpload = uploadFileRepository.findById(fileId);
        if(fileUpload.isPresent()){
            log.info("Found a file-id {}", fileId);
            return loadFileAsResourceByName(fileUpload.get().getName());
        }else{
            throw new FileUploadNotFoundException("File not found ID:" + fileId);
        }
    }
}
