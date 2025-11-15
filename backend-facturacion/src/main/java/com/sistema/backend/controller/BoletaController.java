package com.sistema.backend.controller;

import com.sistema.backend.dto.request.BoletaRequest;
import com.sistema.backend.dto.response.BoletaResponse;
import com.sistema.backend.dto.response.MessageResponse;
import com.sistema.backend.dto.response.BoletaDetailsResponse;
import com.sistema.backend.entity.Boleta;
import com.sistema.backend.entity.DetalleBoleta;
import com.sistema.backend.service.BoletaService;
import com.sistema.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de boletas
 * Requiere autenticación JWT en todos los endpoints
 * 
 * @author Paulo
 */
@RestController
@RequestMapping("/api/boletas")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@Slf4j
public class BoletaController {

    private final BoletaService boletaService;
    private final UsuarioService usuarioService;
    /**
     * POST /api/boletas
     * Crear nueva boleta (checkout)
     * 
     * PROTEGIDO - Requiere JWT
     */
    @PostMapping
    public ResponseEntity<?> crearBoleta(
            @Valid @RequestBody BoletaRequest request,
            Authentication authentication) {
        try {
            // Obtener ID del usuario autenticado desde el token JWT
            Integer idUsuario = (Integer) authentication.getPrincipal();

            log.info("📝 Creando boleta para usuario ID: {}", idUsuario);

            BoletaResponse response = boletaService.procesarBoleta(request, idUsuario);

            if (response.isSuccess()) {
                log.info("✅ Boleta creada exitosamente ID: {}", response.getBoletaId());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            log.error("❌ Error al crear boleta: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BoletaResponse.error("Error al crear la boleta: " + e.getMessage()));
        }
    }

    /**
     * GET /api/boletas
     * Obtener todas las boletas del usuario autenticado
     * 
     * PROTEGIDO - Requiere JWT
     */
    @GetMapping
    public ResponseEntity<?> obtenerMisBoletas(Authentication authentication) {
        try {
            Integer idUsuario = (Integer) authentication.getPrincipal();

            log.info("📋 Obteniendo boletas del usuario ID: {}", idUsuario);

            List<Boleta> boletas = boletaService.getBoletasByUserId(idUsuario);

            List<BoletaDetailsResponse> responseList = boletas.stream()
                    .<BoletaDetailsResponse>map(this::convertToBoletaDetailsResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseList);

        } catch (Exception e) {
            log.error("❌ Error al obtener boletas: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener boletas", false));
        }
    }

    /**
     * GET /api/boletas/ordenadas
     * Obtener boletas ordenadas por fecha descendente
     * 
     * PROTEGIDO - Requiere JWT
     */
    @GetMapping("/ordenadas")
    public ResponseEntity<?> obtenerMisBoletasOrdenadas(Authentication authentication) {
        try {
            Integer idUsuario = (Integer) authentication.getPrincipal();

            log.info("📋 Obteniendo boletas ordenadas del usuario ID: {}", idUsuario);

            List<Boleta> boletas = boletaService.getBoletasByUserIdOrdenadas(idUsuario);

            List<BoletaDetailsResponse> responseList = boletas.stream()
                    .<BoletaDetailsResponse>map(this::convertToBoletaDetailsResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseList);

        } catch (Exception e) {
            log.error("❌ Error al obtener boletas: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener boletas", false));
        }
    }

    /**
     * GET /api/boletas/{id}
     * Obtener una boleta específica por ID
     * 
     * PROTEGIDO - Requiere JWT y verifica propiedad
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerBoletaPorId(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            Integer idUsuario = (Integer) authentication.getPrincipal();

            log.info("🔍 Buscando boleta ID: {} para usuario ID: {}", id, idUsuario);

            Boleta boleta = boletaService.getBoletaByIdAndUser(id, idUsuario);

            BoletaDetailsResponse response = convertToBoletaDetailsResponse(boleta);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.warn("⚠️ {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage(), false));

        } catch (Exception e) {
            log.error("❌ Error al obtener boleta: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener la boleta", false));
        }
    }

    /**
     * GET /api/boletas/{id}/detalles
     * Obtener detalles de una boleta específica
     * 
     * PROTEGIDO - Requiere JWT
     */
    @GetMapping("/{id}/detalles")
    public ResponseEntity<?> obtenerDetallesBoleta(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            Integer idUsuario = (Integer) authentication.getPrincipal();

            log.info("🔍 Obteniendo detalles de boleta ID: {}", id);

            // Primero verificar que la boleta pertenezca al usuario
            boletaService.getBoletaByIdAndUser(id, idUsuario);

            // Obtener los detalles
            List<DetalleBoleta> detalles = boletaService.getDetallesByBoletaId(id);

            // Convierte a DTOs para evitar serialización de proxies
            List<BoletaDetailsResponse.DetalleBoletaDTO> detalleDTOs = detalles.stream()
                    .map(this::convertToDetalleDTO) // Usa un método helper
                    .collect(Collectors.toList());

            return ResponseEntity.ok(detalleDTOs);

        } catch (RuntimeException e) {
            log.warn("⚠️ {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage(), false));

        } catch (Exception e) {
            log.error("❌ Error al obtener detalles: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener detalles", false));
        }
    }

    // Método helper para convertir entidad a DTO
    private BoletaDetailsResponse convertToBoletaDetailsResponse(Boleta boleta) {
        BoletaDetailsResponse response = BoletaDetailsResponse.builder()
                .idBoleta(boleta.getIdBoleta())
                .fecha_creacion(boleta.getFechaCreacion().toString())
                .total(boleta.getTotal())
                .idUsuario(boleta.getIdUsuario())
                .nombreCliente(boleta.getNombreCliente())
                .documentoCliente(boleta.getDocumentoCliente())
                .emailCliente(boleta.getEmailCliente())
                .build();

        // Incluye los detalles: asume que boletaService tiene un método para obtenerlos
        List<DetalleBoleta> detalles = boletaService.getDetallesByBoletaId(boleta.getIdBoleta());
        response.setDetalles(detalles.stream().map(this::convertToDetalleDTO).collect(Collectors.toList()));
        response.setUsuarioVendedor(usuarioService.obtenerUsuarioDTO(boleta.getIdUsuario()));
        return response;
    }

    private BoletaDetailsResponse.DetalleBoletaDTO convertToDetalleDTO(DetalleBoleta detalle) {
        return BoletaDetailsResponse.DetalleBoletaDTO.builder()
                .idDetalle(detalle.getIdDetalle())
                .producto(detalle.getProducto())
                .cantidad(detalle.getCantidad())
                .precio_unitario(detalle.getPrecioUnitario())
                .build();
    }
}
