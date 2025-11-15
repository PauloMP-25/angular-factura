package com.sistema.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que construye la respuesta del Autenticador para mostrarlo en el frontend
 * @author Paulo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    
    // Token personalizados
    private String token;

    // Información del usuario
    private Integer idUsuario;
    private String email;
    private String nombreCompleto;
    
    // Información adicional
    private String documento;

    // Mensaje de respuesta
    private String mensaje;
    private Boolean success;
}
