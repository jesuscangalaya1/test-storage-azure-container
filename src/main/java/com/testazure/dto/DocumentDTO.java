package com.testazure.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DocumentDTO {

    private Long id;
    private String documentName;
    private String documentType;
    private String uploadedBy; // nombre del quién sube el archivo
    private LocalDateTime uploadDate;
    private LocalDateTime lastUpdated;
    private Boolean active;

}
