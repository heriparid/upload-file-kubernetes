package com.heriparid.lab.uploadfile.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class UploadFile {
    @Id
    private Long id;
    private String name;
    private String uri;
    private Date uploadedDate;

    public UploadFile(){}

    public UploadFile(Long id){
        this.id = id;
        this.uploadedDate = new Date();
    }
}
