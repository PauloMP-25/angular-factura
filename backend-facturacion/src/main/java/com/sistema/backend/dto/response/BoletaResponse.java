package com.sistema.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * DTO de respuesta enviado al frontend después del checkout.
 * Incluye información de la boleta creado.
 * @autor Paulo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoletaResponse {
    private boolean success;
    
    private String mensaje;
    
    private Long boletaId;
        
    // Constructor para respuesta de error
    public static BoletaResponse error(String mensaje) {
        return BoletaResponse.builder()
                .success(false)
                .mensaje(mensaje)
                .build();
    }
}
