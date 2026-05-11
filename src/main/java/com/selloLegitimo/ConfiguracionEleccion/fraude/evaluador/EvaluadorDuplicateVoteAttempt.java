package com.selloLegitimo.ConfiguracionEleccion.fraude.evaluador;

import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.AlertaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.EventoVotacionDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.VotingEventContext;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.ReglaAntifraude;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.Severidad;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.TipoEventoVotacion;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.TipoRegla;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class EvaluadorDuplicateVoteAttempt implements EvaluadorRegla {

	@Override
	public TipoRegla getTipoRegla() {
		return TipoRegla.DUPLICATE_VOTE_ATTEMPT;
	}

	@Override
	public Optional<AlertaDto> evaluar(ReglaAntifraude regla, VotingEventContext context) {
		EventoVotacionDto actual = context.getEventoActual();
		if (actual.getTipo() != TipoEventoVotacion.VOTO
			|| actual.getDocumentId() == null || actual.getDocumentId().isBlank()) {
			return Optional.empty();
		}

		Map<String, Object> params = regla.getParameters();
		int ventanaHoras = ParametrosRegla.obtenerEntero(params, "windowHours");

		LocalDateTime referencia = actual.getTimestamp() == null ? LocalDateTime.now() : actual.getTimestamp();
		LocalDateTime desde = referencia.minus(Duration.ofHours(ventanaHoras));

		long votosPrevios = context.getEventosHistoricos().stream()
			.filter(e -> e.getTipo() == TipoEventoVotacion.VOTO)
			.filter(EventoVotacionDto::isExitoso)
			.filter(e -> Objects.equals(e.getDocumentId(), actual.getDocumentId()))
			.filter(e -> e.getTimestamp() != null && !e.getTimestamp().isBefore(desde)
				&& !e.getTimestamp().isAfter(referencia))
			.count();

		if (votosPrevios < 1) {
			return Optional.empty();
		}

		// Cualquier intento duplicado se considera de alta severidad por defecto.
		Severidad severidad = ParametrosRegla.calcularSeveridad(votosPrevios + 1, 1, regla.getRiskScoreWeight());
		Map<String, Object> detalles = new LinkedHashMap<>();
		detalles.put("votosPreviosRegistrados", votosPrevios);
		detalles.put("ventanaHoras", ventanaHoras);

		return Optional.of(AlertaDto.builder()
			.alertType(regla.getAlertType())
			.ruleId(regla.getId())
			.severity(severidad)
			.details(detalles)
			.timestamp(referencia)
			.tableId(actual.getTableId())
			.documentId(actual.getDocumentId())
			.build());
	}
}
