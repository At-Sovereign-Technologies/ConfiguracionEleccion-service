package com.selloLegitimo.ConfiguracionEleccion.fraude.repositorio;

import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.EstadoAprobacion;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.ReglaAntifraude;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioReglaAntifraude extends JpaRepository<ReglaAntifraude, Long> {

	List<ReglaAntifraude> findByApprovalStatusAndIsActiveTrueAndDeletedAtIsNull(EstadoAprobacion estado);

	Optional<ReglaAntifraude> findByNameIgnoreCase(String name);
}
