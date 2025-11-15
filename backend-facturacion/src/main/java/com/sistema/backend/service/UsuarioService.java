package com.sistema.backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sistema.backend.dto.response.UsuarioResponse;
import com.sistema.backend.dto.response.BoletaDetailsResponse.UsuarioDTO;
import com.sistema.backend.entity.Usuario;
import com.sistema.backend.repository.UsuarioRepository;

/**
 * Servicio para gestión de usuarios (CRUD y administración)
 * 
 * @autor Paulo
 */
@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtener usuario por ID
     */
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(Integer.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return convertirAResponse(usuario);
    }

    /**
     * Obtener usuario por email
     */
    @Transactional(readOnly = true)
    public Integer obtenerIdPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getIdUsuario().intValue();
    }

    /**
     * Convertir entidad Usuario a DTO UsuarioResponse
     */
    private UsuarioResponse convertirAResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();

        response.setIdUsuario(usuario.getIdUsuario());
        response.setEmail(usuario.getEmail());
        response.setNombres(usuario.getNombres());
        response.setApellidos(usuario.getApellidos());

        // Datos extendidos
        response.setNumeroDocumento(usuario.getNumeroDocumento());
        response.setNombreCompleto(usuario.getNombres() + " " + usuario.getApellidos());

        return response;
    }

    /**
     * Obtiene el DTO de los datos básicos del usuario (vendedor) para la boleta.
     * Si no lo encuentra, devuelve un DTO con valores por defecto.
     * 
     * @param id ID del usuario (vendedor)
     * @return UsuarioDTO con datos mapeados
     */
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioDTO(Integer id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            // Manejo de caso donde el usuario no existe (seguro histórico)
            return UsuarioDTO.builder()
                    .id(id)
                    .nombres("Vendedor Desconocido")
                    .apellidos("")
                    .numero_documento("N/A")
                    .build();
        }

        Usuario usuario = usuarioOpt.get();

        return UsuarioDTO.builder()
                .id(usuario.getIdUsuario())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .numero_documento(usuario.getNumeroDocumento())
                .build();
    }
}
