package com.sistema.backend.repository;

import com.sistema.backend.entity.DetalleBoleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para entidad DetalleBoleta
 * @autor Paulo
 */
@Repository
public interface DetalleBoletaRepository extends JpaRepository<DetalleBoleta, Integer>{
    
    /**
    * Busca todas los detalles de una boleta asociadas a un id_boleta específico.
    */
    List<DetalleBoleta> findByBoleta_IdBoleta(Integer idBoleta);
}
