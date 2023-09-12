package com.testazure.client;


import com.azure.storage.blob.models.BlobDownloadContentResponse;
import com.azure.storage.blob.models.BlobStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class StorageAzure {

    @Value("${azure.storage.connection-string}")
    private String connectionString;
    @Value("${azure.storage.container-name}")
    private String containerName;

    public String uploadAttachment(byte[] fileAttachment, String nameAttachment, String format) {
        File attachmentTemp = null;
        try {
            attachmentTemp = File.createTempFile(nameAttachment, null);
            FileOutputStream out = new FileOutputStream(attachmentTemp);
            out.write(fileAttachment);
            out.close();
        }catch (IOException e) {
            log.error(e.getMessage());
        }

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        BlobClient blobClient = containerClient.getBlobClient(nameAttachment);
        blobClient.uploadFromFile(attachmentTemp.getAbsolutePath(), false);
        if (attachmentTemp.exists()) {
            attachmentTemp.delete();
        }
        return blobClient.getBlobUrl();
    }

    public String uploadDocument(byte[] fileBytes, String documentName) {
        try {
            log.info("Iniciando la carga del documento en Azure Storage...");

            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

            // Genera un hash del nombre del documento (puedes personalizar la lógica de hash)
            String documentHash = generateDocumentHash(documentName);

            // Genera un nombre de archivo único utilizando el hash
            String uniqueFileName = documentHash + "-" + documentName;

            log.info("Nombre de archivo único generado: {}", uniqueFileName);

            // Crea un BlobClient para el nuevo documento en el contenedor
            BlobClient blobClient = containerClient.getBlobClient(uniqueFileName);

            // Sube el archivo al Blob Storage
            blobClient.upload(new ByteArrayInputStream(fileBytes), fileBytes.length, true);

            log.info("Documento cargado con éxito en Azure Storage. URL del documento: {}", blobClient.getBlobUrl());

            return blobClient.getBlobUrl();
        } catch (Exception e) {
            log.error("Error al cargar el documento en Azure Storage: " + e.getMessage(), e);
            throw new RuntimeException("Error al cargar el documento en Azure Storage.", e);
        }
    }


    public byte[] downloadDocument(String originalDocumentName) {
        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

            // Genera el nombre de archivo único utilizando el hash (como lo hiciste al cargar el archivo)
            String documentHash = generateDocumentHash(originalDocumentName);
            String uniqueFileName = documentHash + "-" + originalDocumentName;

            BlobClient blobClient = containerClient.getBlobClient(uniqueFileName);

            // Descarga el contenido del archivo
            //BinaryData blobData = blobClient.openQueryInputStream();
            //byte[] fileBytes = blobData.toBytes();

            //return fileBytes;

            return null;
        } catch (BlobStorageException e) {
            // Maneja las excepciones apropiadamente según tus necesidades (por ejemplo, loguear el error)
            throw new RuntimeException("Error al descargar el documento desde Azure Storage.", e);
        }

    }


    private String generateDocumentHash(String documentName) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(documentName.getBytes(StandardCharsets.UTF_8));

            // Convierte los bytes del hash a una representación hexadecimal
            StringBuilder hexHash = new StringBuilder();
            for (byte hashByte : hashBytes) {
                hexHash.append(String.format("%02x", hashByte));
            }

            return hexHash.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error al generar el hash SHA-256 del nombre del documento: " + e.getMessage(), e);
            throw new RuntimeException("Error al generar el hash SHA-256 del nombre del documento.", e);
        }
    }



}
