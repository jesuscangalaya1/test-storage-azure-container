package com.testazure.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "documents")
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentName;
    private String documentType;
    private String uploadedBy; // nombre del quién sube el archivo
    private LocalDateTime uploadDate; // Usamos LocalDateTime para incluir fecha y hora de carga
    private LocalDateTime lastUpdated; // Fecha y hora de última actualización

    private Boolean active = true; // Valor por defecto: true

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity user;

    @PrePersist
    protected void onCreate() {
        this.uploadDate = LocalDateTime.now(); // Establece la fecha de creación al momento de persistir
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now(); // Actualiza la fecha de última actualización al momento de actualizar
    }

}
