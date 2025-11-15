package com.sistema.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO principal que recibe el frontend en el checkout.
 * Contiene toda la información necesaria para procesar la boleta.
 * @autor Paulo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoletaRequest {
    // El idUsuario se extraerá del JWT en el controlador
    private Integer idUsuario;

    private Integer idDetalle;

    @NotEmpty(message = "El carrito no puede estar vacío")
    @Valid
    private List<DetalleRequest> cartItems;

    @NotNull(message = "El total es obligatorio")
    private BigDecimal total;

    private BigDecimal subtotal;

    // =========================================
    // DATOS DEL CLIENTE
    // =========================================
    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombreCliente;

    private String documentoCliente;
    
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Email del cliente inválido")
    private String emailCliente;
    
}

