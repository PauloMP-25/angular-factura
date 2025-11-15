package com.sistema.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sistema.backend.dto.request.LoginRequest;
import com.sistema.backend.dto.request.RegistroRequest;
import com.sistema.backend.dto.response.AuthResponse;
import com.sistema.backend.entity.Usuario;
import com.sistema.backend.repository.UsuarioRepository;
import com.sistema.backend.util.JwtUtil;

/**
 * Servicio para gestionar autenticación
 * Maneja registro, login, verificación de tokens
 * @autor Paulo
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Registrar nuevo usuario
     */
    public AuthResponse registrarUsuario(RegistroRequest request) throws Exception {
        logger.info("🔍 Iniciando registro para: {}", request.getEmail());

        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Verificar si el documento ya existe
        if (request.getNumeroDocumento() != null &&
                usuarioRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new IllegalArgumentException("El número de documento ya está registrado");
        }

        try {
            // Crear usuario en PostgreSQL
            Usuario usuario = new Usuario();
            usuario.setEmail(request.getEmail());
            usuario.setNombres(request.getNombres());
            usuario.setApellidos(request.getApellidos());
            usuario.setNumeroDocumento(request.getNumeroDocumento());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));

            Usuario usuarioGuardado = usuarioRepository.save(usuario);

            logger.info("✅ Usuario creado con ID: {}", usuarioGuardado.getIdUsuario());

            // Generar token JWT
            String token = jwtUtil.generateToken(
                    usuarioGuardado.getIdUsuario(),
                    usuarioGuardado.getEmail());

            return AuthResponse.builder()
                    .token(token)
                    .idUsuario(usuarioGuardado.getIdUsuario())
                    .email(usuarioGuardado.getEmail())
                    .nombreCompleto(usuarioGuardado.getNombres() + " " + usuarioGuardado.getApellidos())
                    .documento(usuarioGuardado.getNumeroDocumento())
                    .mensaje("Usuario registrado exitosamente")
                    .success(true)
                    .build();

        } catch (Exception e) {
            logger.error("❌ Error al crear usuario: {}", e.getMessage());
            throw new Exception("Error al registrar usuario: " + e.getMessage());
        }
    }

    /**
     * Iniciar sesión
     */
    public AuthResponse iniciarSesion(LoginRequest request) throws Exception {
        logger.info("🔍 Iniciando sesión para: {}", request.getEmail());

        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("El usuario no esta registrado"));

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña es incorrecta");
        }

        logger.info("✅ Login exitoso para: {}", request.getEmail());

        // Generar token JWT
        String token = jwtUtil.generateToken(usuario.getIdUsuario(), usuario.getEmail());

        return AuthResponse.builder()
                .token(token)
                .idUsuario(usuario.getIdUsuario())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombres() + " " + usuario.getApellidos())
                .documento(usuario.getNumeroDocumento())
                .mensaje("Login exitoso")
                .success(true)
                .build();
    }

    /**
     * Verificar token JWT
     */
    public AuthResponse verificarToken(String token) throws Exception {
        try {
            logger.info("🔍 Verificando token...");

            if (!jwtUtil.validateToken(token)) {
                throw new Exception("Token inválido");
            }

            Integer idUsuario = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);

            logger.info("✅ Token válido para ID: {}", idUsuario);

            // Obtener usuario desde PostgreSQL
            Usuario usuario = usuarioRepository.findById(Integer.valueOf(idUsuario))
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            return AuthResponse.builder()
                    .token(token)
                    .idUsuario(usuario.getIdUsuario())
                    .email(email)
                    .nombreCompleto(usuario.getNombres() + " " + usuario.getApellidos())
                    .documento(usuario.getNumeroDocumento())
                    .mensaje("Token válido")
                    .success(true)
                    .build();

        } catch (Exception e) {
            logger.error("❌ Error al verificar token: {}", e.getMessage());
            throw new Exception("Token inválido o expirado");
        }
    }

    /**
     * Refrescar token
     */
    public AuthResponse refrescarToken(String oldToken) throws Exception {
        try {
            logger.info("🔄 Refrescando token...");

            if (!jwtUtil.validateToken(oldToken)) {
                throw new Exception("Token inválido");
            }

            Integer idUsuario = jwtUtil.getUserIdFromToken(oldToken);

            // Generar nuevo token
            Usuario usuario = usuarioRepository.findById(Integer.valueOf(idUsuario))
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            String newToken = jwtUtil.generateToken(usuario.getIdUsuario(), usuario.getEmail());

            logger.info("✅ Token refrescado para ID: {}", idUsuario);

            return AuthResponse.builder()
                    .token(newToken)
                    .idUsuario(usuario.getIdUsuario())
                    .email(usuario.getEmail())
                    .nombreCompleto(usuario.getNombres() + " " + usuario.getApellidos())
                    .documento(usuario.getNumeroDocumento())
                    .mensaje("Token refrescado exitosamente")
                    .success(true)
                    .build();

        } catch (Exception e) {
            logger.error("❌ Error al refrescar token: {}", e.getMessage());
            throw new Exception("Error al refrescar token");
        }
    }

    /**
     * Verificar si un email está disponible
     */
    @Transactional(readOnly = true)
    public boolean verificarEmailDisponible(String email) {
        return !usuarioRepository.existsByEmail(email);
    }
}

