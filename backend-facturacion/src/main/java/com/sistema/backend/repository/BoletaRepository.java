package com.sistema.backend.repository;

import com.sistema.backend.entity.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para entidad Boleta
 * @autor Paulo
 */
@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Integer> {

        /**
         * Busca todas las boletas asociadas a un usuario específico
         */
        List<Boleta> findByIdUsuario(Integer idUsuario);

        /**
         * Busca una boleta por su ID
         */
        Optional<Boleta> findByIdBoleta(Integer idBoleta);

        /**
         * Busca todas las boletas de un usuario ordenadas por fecha descendente
         * CORREGIDO: Usa fechaCreacion en lugar de fechaBoleta
         */
        List<Boleta> findByIdUsuarioOrderByFechaCreacionDesc(Integer idUsuario);

        /**
         * Verifica si una boleta existe por su ID
         */
        boolean existsByIdBoleta(Integer idBoleta);
}
