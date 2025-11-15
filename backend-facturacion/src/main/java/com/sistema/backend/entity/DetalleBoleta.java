package com.sistema.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Entidad DetalleBoleta
 * @author user
 */
@Entity
@Table(name = "detalle_boleta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleBoleta{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_boleta", nullable = false)
    private Boleta boleta;

    @Column(name = "producto", nullable = false, length = 150)
    private String producto;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    // Método de utilidad para calcular subtotal
    public void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        calcularSubtotal();
    }
}


