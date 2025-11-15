package com.sistema.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sistema.backend.entity.Usuario;
import java.util.Optional;

/**
 * Repositorio para Usuario
 * @autor Paulo
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Buscar usuario por email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Buscar usuario por número de documento
     */
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);

    /**
     * Verificar si existe usuario por email
     */
    boolean existsByEmail(String email);

    /**
     * Verificar si existe usuario por número de documento
     */
    boolean existsByNumeroDocumento(String numeroDocumento);
}

