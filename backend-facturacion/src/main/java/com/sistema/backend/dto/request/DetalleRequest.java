package com.sistema.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO que representa un item en un carrito de compras.
 * @autor Paulo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleRequest {

    @NotNull(message = "El nombre del producto es obligatorio")
    private String nombreProducto;

    @NotNull(message = "El precio unitario es obligatorio")
    @Min(value = 0, message = "El precio debe ser mayor a 0")
    private BigDecimal precioUnitario;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
}

