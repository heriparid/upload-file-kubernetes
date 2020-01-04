package com.heriparid.lab.uploadfile.controller;

import com.heriparid.lab.uploadfile.service.IStorageService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;

@RunWith(MockitoJUnitRunner.class)
public class FileResourceControllerTests {

    private InputStream inputStream;
    private MockMvc mockMvc;

    @Mock
    private IStorageService storageService;

    @InjectMocks
    private FileResourceController fileUploadController = new FileResourceController();

    @Before
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(fileUploadController).build();
        inputStream = fileUploadController.getClass().getClassLoader().getResourceAsStream("application.yaml");
    }

    @Test
    public void testUploadFile() throws Exception {
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile(
                        "file",
                        "application.yaml",
                        "multipart/form-data",
                        inputStream);

        MvcResult result = mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .fileUpload("/upload")
                                .file(mockMultipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

        Mockito.verify(storageService, Mockito.times(1)).storeFile(mockMultipartFile);

        Assert.assertEquals(200, result.getResponse().getStatus());
        Assert.assertNotNull(result.getResponse().getContentAsString());
        // TODO assert orginal-file given name as same as result uploaded file
    }
}
