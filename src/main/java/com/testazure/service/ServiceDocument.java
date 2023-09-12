package com.testazure.service;

import com.testazure.dto.DocumentDTO;
import com.testazure.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceDocument {

    private final DocumentRepository documentRepository;

    @Transactional
    public void uploadDocument(String documentName, String documentType, String uploadedBy, Long userId) {
        // Verifica que el usuario tenga permisos para cargar documentos (puedes implementar esta lógica según tus necesidades)
        UserDTO user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (!"Service Manager".equals(user.getRole())) {
            throw new UnauthorizedException("No tienes permisos para cargar documentos.");
        }

        // Realiza la carga del documento en la base de datos
        documentRepository.uploadDocument(documentName, documentType, uploadedBy, userId);
    }

    @Transactional
    public void deleteDocumentLogical(Long documentId) {
        // Verifica si el documento existe
        DocumentDTO document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"));

        // Realiza la eliminación lógica y establece la fecha de última actualización
        document.setActive(false);
        document.setLastUpdated(LocalDateTime.now());

        // Guarda el documento actualizado en la base de datos
        documentRepository.save(document);
    }


}