package com.selloLegitimo.ConfiguracionEleccion.fraude.repositorio;

import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.RegistroAuditoriaRegla;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioAuditoriaRegla extends JpaRepository<RegistroAuditoriaRegla, Long> {

	List<RegistroAuditoriaRegla> findByRuleIdOrderByCreatedAtDesc(Long ruleId);
}
