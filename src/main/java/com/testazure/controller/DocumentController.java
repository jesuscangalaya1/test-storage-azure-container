package com.testazure.controller;

import com.testazure.client.StorageAzure;
import com.testazure.service.ServiceDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final ServiceDocument serviceDocument;
    private final StorageAzure storageAzure;

    @GetMapping("/download/{documentName}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable String documentName) {
        // Obtener la URL de descarga del servicio
        String downloadUrl = Arrays.toString(storageAzure.downloadDocument(documentName));

        // Redirigir al usuario a la URL de descarga
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(downloadUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }






}
