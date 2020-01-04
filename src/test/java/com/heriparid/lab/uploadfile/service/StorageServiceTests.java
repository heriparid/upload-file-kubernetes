package com.heriparid.lab.uploadfile.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(MockitoJUnitRunner.class)
public class StorageServiceTests {

    private final String path = "./tmp";

    @InjectMocks
    private StorageService storageService;
    private InputStream inputStream;

    @Before
    public void init() throws IOException {
        storageService = new StorageService(path);
        inputStream = storageService.getClass().getClassLoader().getResourceAsStream("application.yaml");
        Files.createDirectory(Paths.get(path));
    }

    @After
    public void deinit() throws IOException {
        FileSystemUtils.deleteRecursively(Paths.get(path));
    }

    @Test
    public void testUploadFile() throws Exception {
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile(
                        "file",
                        "application.yaml",
                        "multipart/form-data",
                        inputStream);

        boolean result = storageService.uploadFile(mockMultipartFile);
        Assert.assertEquals(true, result);
    }
}
