package com.selloLegitimo.ConfiguracionEleccion.repositorio;

import com.selloLegitimo.ConfiguracionEleccion.modelo.Eleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioEleccion extends JpaRepository<Eleccion, Long> {
}