package com.selloLegitimo.ConfiguracionEleccion.fraude.controlador;

import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.FraudEvaluationResult;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.VotingEventContext;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.Rol;
import com.selloLegitimo.ConfiguracionEleccion.fraude.seguridad.RolPermitido;
import com.selloLegitimo.ConfiguracionEleccion.fraude.servicio.FraudEngineService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraud-engine")
public class ControladorMotorAntifraude {

	private static final Logger logger = LoggerFactory.getLogger(ControladorMotorAntifraude.class);

	@Autowired
	private FraudEngineService fraudEngineService;

	@PostMapping("/evaluate")
	@RolPermitido({ Rol.ADMIN_RNEC, Rol.DELEGADO_CNE, Rol.ADMINISTRADOR, Rol.SUPERADMIN, Rol.AUDITOR, Rol.OPERADOR })
	public ResponseEntity<FraudEvaluationResult> evaluar(@Valid @RequestBody VotingEventContext contexto) {
		logger.info("Evaluacion antifraude mesa {} documento {}",
			contexto.getEventoActual().getTableId(),
			contexto.getEventoActual().getDocumentId());
		return ResponseEntity.ok(fraudEngineService.evaluateEvent(contexto));
	}
}
