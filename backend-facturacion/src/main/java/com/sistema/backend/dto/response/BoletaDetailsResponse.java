package com.sistema.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.math.BigDecimal;

/**
 * DTO para respuesta detallada de Boleta, incluyendo detalles.
 * @author Paulo
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoletaDetailsResponse {
    private Integer idBoleta;
    private Integer idUsuario;
    private String fecha_creacion;
    private BigDecimal total;
    
// =========================================
    // DATOS DEL CLIENTE (NUEVOS CAMPOS)
    // =========================================
    private String nombreCliente;
    private String documentoCliente;
    private String emailCliente;
    
    private List<DetalleBoletaDTO> detalles;
    private UsuarioDTO usuarioVendedor;
    
    // Sub-DTO para DetalleBoleta
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetalleBoletaDTO {
        private Integer idDetalle;
        private String producto;
        private BigDecimal precio_unitario;
        private Integer cantidad;
        private BigDecimal subtotal;
    }
    
    // Sub-DTO para Usuario, si es necesario
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsuarioDTO {
        private Integer id;
        private String nombres;
        private String apellidos;
        private String numero_documento;
    }
}
