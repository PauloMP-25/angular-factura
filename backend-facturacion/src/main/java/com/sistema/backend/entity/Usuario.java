package com.sistema.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Usuario
 * @autor Paulo
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    // =========================================
    // IDENTIFICADORES Y RELACIONES
    // =========================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    // =========================================
    // DATOS BÁSICOS
    // =========================================
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;


    @Column(name = "numero_documento", unique = true, length = 20)
    private String numeroDocumento;

    @Column(name = "password", nullable = false)
    private String password = "";
}
