package com.sistema.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.sistema.backend.dto.response.AuthResponse;
import com.sistema.backend.dto.request.RegistroRequest;
import com.sistema.backend.dto.request.LoginRequest;
import com.sistema.backend.dto.response.MessageResponse;
import com.sistema.backend.dto.response.UsuarioResponse;
import com.sistema.backend.service.AuthService;
import com.sistema.backend.service.UsuarioService;

/**
 * Controlador REST para gestión de usuarios y autenticación
 * Requiere autenticación JWT en dos endpoints 
 * @author user
 */

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthService authService;

    /**
     * POST /api/usuarios/registro
     * Registrar nuevo usuario
     * 
     * PÚBLICO - No requiere autenticación
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@Validated @RequestBody RegistroRequest request) {
        try {
            logger.info("🔍 Intentando registrar usuario: {}", request.getEmail());

            AuthResponse response = authService.registrarUsuario(request);

            logger.info("✅ Usuario registrado exitosamente: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ Error de validación en registro: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage(), false));

        } catch (Exception e) {
            logger.error("❌ Error inesperado en registro: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al registrar usuario: " + e.getMessage(), false));
        }
    }

    /**
     * POST /api/usuarios/login
     * Iniciar sesión
     * 
     * PÚBLICO - No requiere autenticación
     */
    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@Validated @RequestBody LoginRequest request) {
        try {
            logger.info("🔍 Intento de login para: {}", request.getEmail());

            AuthResponse response = authService.iniciarSesion(request);

            logger.info("✅ Login exitoso para: {}", request.getEmail());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ Credenciales inválidas para: {}", request.getEmail());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(e.getMessage(), false));

        } catch (Exception e) {
            logger.error("❌ Error en login: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al iniciar sesión: " + e.getMessage(), false));
        }
    }

    /**
     * POST /api/usuarios/verificar-token
     * Verificar si el token JWT es válido
     * 
     * PÚBLICO - Útil para validar tokens en el frontend
     */
    @PostMapping("/verificar-token")
    public ResponseEntity<?> verificarToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse("Token no proporcionado", false));
            }

            String token = authHeader.substring(7);
            logger.info("🔍 Verificando token...");

            AuthResponse response = authService.verificarToken(token);

            logger.info("✅ Token válido para: {}", response.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.warn("❌ Token inválido: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Token inválido o expirado", false));
        }
    }

    /**
     * POST /api/usuarios/refrescar-token
     * Refrescar token JWT
     * 
     * PÚBLICO - Útil para renovar tokens antes de que expiren
     */
    @PostMapping("/refrescar-token")
    public ResponseEntity<?> refrescarToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse("Token no proporcionado", false));
            }

            String token = authHeader.substring(7);
            logger.info("🔄 Refrescando token...");

            AuthResponse response = authService.refrescarToken(token);

            logger.info("✅ Token refrescado exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.warn("❌ Error al refrescar token: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Error al refrescar token", false));
        }
    }

    /**
     * GET /api/usuarios/verificar-email/{email}
     * Verificar si un email ya está registrado
     * 
     * PÚBLICO - Útil para validación en tiempo real
     */
    @GetMapping("/verificar-email/{email}")
    public ResponseEntity<?> verificarEmailDisponible(@PathVariable String email) {
        try {
            boolean disponible = authService.verificarEmailDisponible(email);

            return ResponseEntity.ok(new MessageResponse(
                    disponible ? "Email disponible" : "Email ya registrado",
                    disponible));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al verificar email", false));
        }
    }

    /**
     * GET /api/usuarios/perfil
     * Obtener perfil del usuario autenticado
     * 
     * PROTEGIDO - Requiere JWT
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(Authentication authentication) {
        try {
            Integer idUsuario = (Integer) authentication.getPrincipal();

            logger.info("🔍 Obteniendo perfil para usuario ID: {}", idUsuario);

            UsuarioResponse usuario = usuarioService.obtenerPorId(idUsuario);

            return ResponseEntity.ok(usuario);

        } catch (Exception e) {
            logger.error("❌ Error al obtener perfil: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener perfil", false));
        }
    }

    /**
     * GET /api/usuarios/{id}
     * Obtener usuario por ID
     * 
     * PROTEGIDO - Requiere JWT
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Integer id) {
        try {
            logger.info("🔍 Obteniendo usuario ID: {}", id);

            UsuarioResponse usuario = usuarioService.obtenerPorId(id);

            return ResponseEntity.ok(usuario);

        } catch (RuntimeException e) {
            logger.warn("⚠️ Usuario no encontrado: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage(), false));

        } catch (Exception e) {
            logger.error("❌ Error al obtener usuario: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener usuario", false));
        }
    }
}
