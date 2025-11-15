package com.sistema.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Boleta
 * @autor Paulo
 */
@Entity
@Table(name = "boletas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Boleta {
    // =========================================
    // IDENTIFICADORES Y RELACIONES
    // =========================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_boleta")
    private Integer idBoleta;
    
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario; // INT NOT NULL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    private Usuario usuario; // Relación con la entidad Usuario (existente)

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // =========================================
    // DATOS DEL CLIENTE (DESNORMALIZADOS)
    // =========================================
    @Column(name = "nombre_cliente", nullable = false, length = 150)
    private String nombreCliente;
    
    @Column(name = "documento_cliente", length = 20)
    private String documentoCliente; // DNI, RUC, etc.
    
    @Column(name = "email_cliente", length = 150)
    private String emailCliente;
    
    // Relaciones
    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleBoleta> detalles = new ArrayList<>();
    
    // Métodos de utilidad
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public void agregarDetalle(DetalleBoleta detalle) {
        detalles.add(detalle);
        detalle.setBoleta(this);
    }
}
