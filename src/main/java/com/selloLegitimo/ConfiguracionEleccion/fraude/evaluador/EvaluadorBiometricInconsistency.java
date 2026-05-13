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
public class EvaluadorBiometricInconsistency implements EvaluadorRegla {

	@Override
	public TipoRegla getTipoRegla() {
		return TipoRegla.BIOMETRIC_INCONSISTENCY;
	}

	@Override
	public Optional<AlertaDto> evaluar(ReglaAntifraude regla, VotingEventContext context) {
		Map<String, Object> params = regla.getParameters();
		int umbral = ParametrosRegla.obtenerEntero(params, "maxInconsistencies");
		int ventanaHoras = ParametrosRegla.obtenerEntero(params, "windowHours");

		EventoVotacionDto actual = context.getEventoActual();
		if (actual.getDocumentId() == null || actual.getDocumentId().isBlank()) {
			return Optional.empty();
		}

		LocalDateTime referencia = actual.getTimestamp() == null ? LocalDateTime.now() : actual.getTimestamp();
		LocalDateTime desde = referencia.minus(Duration.ofHours(ventanaHoras));

		long inconsistencias = context.getEventosHistoricos().stream()
			.filter(e -> e.getTipo() == TipoEventoVotacion.AUTENTICACION)
			.filter(e -> Objects.equals(e.getDocumentId(), actual.getDocumentId()))
			.filter(e -> !e.isCoincidenciaBiometrica())
			.filter(e -> e.getTimestamp() != null && !e.getTimestamp().isBefore(desde)
				&& !e.getTimestamp().isAfter(referencia))
			.count();

		if (!actual.isCoincidenciaBiometrica() && actual.getTipo() == TipoEventoVotacion.AUTENTICACION) {
			inconsistencias += 1;
		}

		if (inconsistencias < umbral) {
			return Optional.empty();
		}

		Severidad severidad = ParametrosRegla.calcularSeveridad(inconsistencias, umbral, regla.getRiskScoreWeight());
		Map<String, Object> detalles = new LinkedHashMap<>();
		detalles.put("inconsistencias", inconsistencias);
		detalles.put("umbral", umbral);
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
