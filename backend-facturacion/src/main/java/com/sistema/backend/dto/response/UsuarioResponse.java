package com.sistema.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta con datos completos del usuario
 * @autor Paulo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Integer idUsuario;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private String email;

    // Datos extendidos
    private String numeroDocumento;
}

