package com.sistema.backend.service;

import com.sistema.backend.dto.response.BoletaResponse;
import com.sistema.backend.dto.request.DetalleRequest;
import com.sistema.backend.dto.request.BoletaRequest;
import com.sistema.backend.entity.Boleta;
import com.sistema.backend.entity.DetalleBoleta;
import com.sistema.backend.repository.DetalleBoletaRepository;
import com.sistema.backend.repository.BoletaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar la entidad boleta
 * CRUD y administracion
 * @autor Paulo
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoletaService {

    private final BoletaRepository boletaRepository;
    private final DetalleBoletaRepository detalleBoletaRepository;

    /**
     * Obtener boleta por ID verificando que pertenezca al usuario
     */
    public Boleta getBoletaByIdAndUser(Integer idBoleta, Integer idUsuario) {
        Optional<Boleta> boletaOptional = boletaRepository.findByIdBoleta(idBoleta);

        if (boletaOptional.isPresent()) {
            Boleta boleta = boletaOptional.get();
            if (boleta.getIdUsuario().equals(idUsuario)) {
                return boleta;
            } else {
                throw new RuntimeException("La boleta no pertenece al usuario");
            }
        } else {
            throw new RuntimeException("Boleta no encontrada");
        }
    }

    /**
     * Obtener todas las boletas de un usuario
     */
    public List<Boleta> getBoletasByUserId(Integer idUsuario) {
        return boletaRepository.findByIdUsuario(idUsuario);
    }

    /**
     * Obtener todas las boletas de un usuario ordenadas por fecha
     */
    public List<Boleta> getBoletasByUserIdOrdenadas(Integer idUsuario) {
        return boletaRepository.findByIdUsuarioOrderByFechaCreacionDesc(idUsuario);
    }

    /**
     * MÉTODO PRINCIPAL: Procesa una boleta completa de forma transaccional
     */
    @Transactional
    public BoletaResponse procesarBoleta(BoletaRequest request, Integer idUsuario) {
        try {
            log.info("Iniciando procesamiento de boleta para usuario: {}", idUsuario);

            // Validar que el carrito no esté vacío
            if (request.getCartItems() == null || request.getCartItems().isEmpty()) {
                return BoletaResponse.error("El carrito está vacío");
            }

            // Validar que el total sea correcto
            BigDecimal totalCalculado = calcularTotal(request.getCartItems());
            if (totalCalculado.compareTo(request.getTotal()) != 0) {
                log.warn("Total enviado ({}) no coincide con total calculado ({})",
                        request.getTotal(), totalCalculado);
            }

            // Crear boleta
            Boleta boleta = crearBoleta(request, idUsuario);

            // Crear detalles
            crearDetallesBoleta(boleta, request.getCartItems());

            log.info("✅ Boleta {} procesada exitosamente", boleta.getIdBoleta());

            return BoletaResponse.builder()
                    .success(true)
                    .mensaje("Boleta creada exitosamente")
                    .boletaId(boleta.getIdBoleta().longValue())
                    .build();

        } catch (Exception e) {
            log.error("❌ Error al procesar boleta", e);
            return BoletaResponse.error("Error al procesar la boleta: " + e.getMessage());
        }
    }

    /**
     * Crea la entidad Boleta
     */
    private Boleta crearBoleta(BoletaRequest request, Integer idUsuario) {
        Boleta boleta = new Boleta();
        boleta.setIdUsuario(idUsuario);
        boleta.setTotal(request.getTotal());
        boleta.setFechaCreacion(LocalDateTime.now());
        
        boleta.setNombreCliente(request.getNombreCliente());
        boleta.setDocumentoCliente(request.getDocumentoCliente());
        boleta.setEmailCliente(request.getEmailCliente());

        return boletaRepository.save(boleta);
    }

    /**
     * Crea los detalles de la boleta
     */
    private void crearDetallesBoleta(Boleta boleta, List<DetalleRequest> items) {
        for (DetalleRequest item : items) {
            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setBoleta(boleta);
            detalle.setProducto(item.getNombreProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.calcularSubtotal();

            detalleBoletaRepository.save(detalle);
        }
        log.info("✅ Detalles de la boleta creados: {} items", items.size());
    }

    /**
     * Calcula el total de los items
     */
    private BigDecimal calcularTotal(List<DetalleRequest> items) {
        return items.stream()
                .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Obtener detalles de una boleta
     */
    public List<DetalleBoleta> getDetallesByBoletaId(Integer idBoleta) {
        return detalleBoletaRepository.findByBoleta_IdBoleta(idBoleta);
    }
}

