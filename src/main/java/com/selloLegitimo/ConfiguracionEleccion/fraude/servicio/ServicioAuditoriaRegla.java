package com.selloLegitimo.ConfiguracionEleccion.fraude.servicio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.AccionAuditoria;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.EstadoAprobacion;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.RegistroAuditoriaRegla;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.ReglaAntifraude;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.Rol;
import com.selloLegitimo.ConfiguracionEleccion.fraude.repositorio.RepositorioAuditoriaRegla;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioAuditoriaRegla {

	private static final Logger logger = LoggerFactory.getLogger(ServicioAuditoriaRegla.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Autowired
	private RepositorioAuditoriaRegla repositorioAuditoria;

	@Transactional
	public void registrar(ReglaAntifraude regla, AccionAuditoria accion,
		EstadoAprobacion estadoAnterior, EstadoAprobacion estadoNuevo,
		String usuario, Rol rolUsuario, String motivo) {

		RegistroAuditoriaRegla registro = new RegistroAuditoriaRegla();
		registro.setRuleId(regla.getId());
		registro.setAccion(accion);
		registro.setEstadoAnterior(estadoAnterior);
		registro.setEstadoNuevo(estadoNuevo);
		registro.setUsuario(usuario);
		registro.setRolUsuario(rolUsuario);
		registro.setMotivo(motivo);
		registro.setSnapshot(serializarSnapshot(regla));

		repositorioAuditoria.save(registro);
		logger.info("Auditoria regla {} accion {} usuario {} ({} -> {})",
			regla.getId(), accion, usuario, estadoAnterior, estadoNuevo);
	}

	@Transactional(readOnly = true)
	public List<RegistroAuditoriaRegla> listarAuditoria(Long ruleId) {
		return repositorioAuditoria.findByRuleIdOrderByCreatedAtDesc(ruleId);
	}

	private String serializarSnapshot(ReglaAntifraude regla) {
		Map<String, Object> datos = new LinkedHashMap<>();
		datos.put("id", regla.getId());
		datos.put("name", regla.getName());
		datos.put("ruleType", regla.getRuleType());
		datos.put("isActive", regla.getIsActive());
		datos.put("parameters", regla.getParameters());
		datos.put("alertType", regla.getAlertType());
		datos.put("riskScoreWeight", regla.getRiskScoreWeight());
		datos.put("approvalStatus", regla.getApprovalStatus());
		try {
			return MAPPER.writeValueAsString(datos);
		} catch (Exception e) {
			logger.warn("No se pudo serializar snapshot de auditoria para regla {}", regla.getId());
			return null;
		}
	}
}
