package com.heriparid.lab.uploadfile.service;

import com.heriparid.lab.uploadfile.component.SequenceGenerator;
import com.heriparid.lab.uploadfile.model.IUploadFileRepository;
import com.heriparid.lab.uploadfile.model.UploadFile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

@RunWith(MockitoJUnitRunner.class)
public class StorageServiceTests {

    private final String path = "./tmp";
    private final SequenceGenerator sequenceGenerator = new SequenceGenerator();

    @Mock
    private IUploadFileRepository uploadFileRepository;

    private StorageService storageService;
    private InputStream inputStream;

    @Before
    public void init() throws IOException {
        storageService = new StorageService(path, sequenceGenerator, uploadFileRepository);
        inputStream = storageService.getClass().getClassLoader().getResourceAsStream("application.yaml");
    }

    @After
    public void deinit() throws IOException {
        FileSystemUtils.deleteRecursively(Paths.get(path));
    }

    @Test
    public void testUploadFile() throws Exception {
        // Given
        String givenFileName = "application.yaml";
        // Expected Result
        UploadFile uploadFile = new UploadFile(sequenceGenerator.nextId());
        uploadFile.setName(givenFileName);

        MockMultipartFile mockMultipartFile =
                new MockMultipartFile(
                        "file",
                        givenFileName,
                        "multipart/form-data",
                        inputStream);

        Mockito.when(uploadFileRepository.save(Mockito.any())).thenReturn(uploadFile);

        UploadFile result = storageService.storeFile(mockMultipartFile);

        Assert.assertNotNull(result.getId());
        Assert.assertEquals(givenFileName, result.getName());
    }
}
