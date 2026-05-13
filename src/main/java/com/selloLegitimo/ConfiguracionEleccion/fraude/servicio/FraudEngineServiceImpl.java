package com.selloLegitimo.ConfiguracionEleccion.fraude.servicio;

import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.AlertaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.EvaluacionReglaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.FraudEvaluationResult;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.VotingEventContext;
import com.selloLegitimo.ConfiguracionEleccion.fraude.evaluador.EvaluadorRegla;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.EstadoAprobacion;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.ReglaAntifraude;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.TipoRegla;
import com.selloLegitimo.ConfiguracionEleccion.fraude.repositorio.RepositorioReglaAntifraude;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FraudEngineServiceImpl implements FraudEngineService {

	private static final Logger logger = LoggerFactory.getLogger(FraudEngineServiceImpl.class);

	private final RepositorioReglaAntifraude repositorioReglas;
	private final Map<TipoRegla, EvaluadorRegla> evaluadoresPorTipo;

	@Autowired
	public FraudEngineServiceImpl(RepositorioReglaAntifraude repositorioReglas, List<EvaluadorRegla> evaluadores) {
		this.repositorioReglas = repositorioReglas;
		this.evaluadoresPorTipo = new EnumMap<>(TipoRegla.class);
		for (EvaluadorRegla evaluador : evaluadores) {
			this.evaluadoresPorTipo.put(evaluador.getTipoRegla(), evaluador);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public FraudEvaluationResult evaluateEvent(VotingEventContext eventContext) {
		if (eventContext == null || eventContext.getEventoActual() == null) {
			throw new IllegalArgumentException("El contexto del evento es obligatorio");
		}

		List<ReglaAntifraude> reglasActivas = repositorioReglas
			.findByApprovalStatusAndIsActiveTrueAndDeletedAtIsNull(EstadoAprobacion.APPROVED);

		List<AlertaDto> alertas = new ArrayList<>();
		List<EvaluacionReglaDto> detalles = new ArrayList<>();
		int scoreAcumulado = 0;

		for (ReglaAntifraude regla : reglasActivas) {
			EvaluadorRegla evaluador = evaluadoresPorTipo.get(regla.getRuleType());
			if (evaluador == null) {
				logger.warn("No existe evaluador para regla {} de tipo {}", regla.getId(), regla.getRuleType());
				continue;
			}

			EvaluacionReglaDto.EvaluacionReglaDtoBuilder detalle = EvaluacionReglaDto.builder()
				.ruleId(regla.getId())
				.ruleName(regla.getName())
				.ruleType(regla.getRuleType());

			try {
				Optional<AlertaDto> resultado = evaluador.evaluar(regla, eventContext);
				if (resultado.isPresent()) {
					alertas.add(resultado.get());
					scoreAcumulado += regla.getRiskScoreWeight();
					detalle.disparada(true)
						.puntajeAportado(regla.getRiskScoreWeight())
						.mensaje("Regla disparada");
				} else {
					detalle.disparada(false)
						.puntajeAportado(0)
						.mensaje("Sin coincidencias");
				}
			} catch (Exception e) {
				logger.error("Error evaluando regla {}: {}", regla.getId(), e.getMessage());
				detalle.disparada(false)
					.puntajeAportado(0)
					.mensaje("Error al evaluar: " + e.getMessage());
			}

			detalles.add(detalle.build());
		}

		int scoreFinal = Math.min(100, scoreAcumulado);
		logger.info("Evaluacion antifraude para mesa {} documento {} -> {} alertas, score {}",
			eventContext.getEventoActual().getTableId(),
			eventContext.getEventoActual().getDocumentId(),
			alertas.size(),
			scoreFinal);

		return FraudEvaluationResult.builder()
			.alerts(alertas)
			.totalRiskScore(scoreFinal)
			.evaluatedRules(detalles)
			.build();
	}
}
