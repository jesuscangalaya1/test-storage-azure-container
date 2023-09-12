package com.testazure.repository;

import com.testazure.dto.DocumentDTO;
import com.testazure.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/*
CREATE TABLE documents (
        id INT AUTO_INCREMENT PRIMARY KEY,
        documentName VARCHAR(255),
        documentType VARCHAR(255),
        uploadedBy VARCHAR(255),
        uploadDate DATETIME,
        lastUpdated DATETIME,
        active TINYINT(1) DEFAULT 1, -- Valor por defecto: true (1)
        user_id INT,
        FOREIGN KEY (user_id) REFERENCES users (COD_USER)
        );

        CREATE TABLE users (
        COD_USER INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255),
        role VARCHAR(255)
        );
*/

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    // Consulta nativa para obtener todos los documentos filtrados por nombre de documento y usuario
    @Query(value = "SELECT * FROM documents WHERE documentName = ?1 AND uploadedBy = ?2", nativeQuery = true)
    List<DocumentDTO> findDocumentsByNameAndUser(String documentName, String uploadedBy);

    // Consulta nativa para obtener todos los documentos filtrados por fecha de carga
    @Query(value = "SELECT * FROM documents WHERE uploadDate BETWEEN ?1 AND ?2", nativeQuery = true)
    List<DocumentDTO> findDocumentsByUploadDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // Consulta nativa para obtener todos los documentos cargados por un usuario específico
    @Query(value = "SELECT * FROM documents WHERE user_id = ?1", nativeQuery = true)
    List<DocumentDTO> findDocumentsByUserId(Long userId);


    // Consulta nativa para cargar un nuevo documento en la base de datos
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO documents (documentName, documentType, uploadedBy, user_id) " +
                   "VALUES (?1, ?2, ?3, ?4)", nativeQuery = true)
    void uploadDocument(String documentName, String documentType, String uploadedBy, Long userId);


    // Consulta nativa para realizar una eliminación lógica de un documento
    @Modifying
    @Transactional
    @Query(value = "UPDATE documents SET active = false, lastUpdated = ?2 WHERE id = ?1", nativeQuery = true)
    void deleteDocumentLogical(Long documentId, LocalDateTime deletionTimestamp);



}










