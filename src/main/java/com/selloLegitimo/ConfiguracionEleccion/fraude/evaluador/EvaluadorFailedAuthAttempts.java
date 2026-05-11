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
public class EvaluadorFailedAuthAttempts implements EvaluadorRegla {

	@Override
	public TipoRegla getTipoRegla() {
		return TipoRegla.FAILED_AUTH_ATTEMPTS;
	}

	@Override
	public Optional<AlertaDto> evaluar(ReglaAntifraude regla, VotingEventContext context) {
		Map<String, Object> params = regla.getParameters();
		int umbral = ParametrosRegla.obtenerEntero(params, "maxFailedAttempts");
		int ventanaMinutos = ParametrosRegla.obtenerEntero(params, "windowMinutes");

		EventoVotacionDto actual = context.getEventoActual();
		LocalDateTime referencia = actual.getTimestamp() == null ? LocalDateTime.now() : actual.getTimestamp();
		LocalDateTime desde = referencia.minus(Duration.ofMinutes(ventanaMinutos));

		long fallidos = context.getEventosHistoricos().stream()
			.filter(e -> e.getTipo() == TipoEventoVotacion.AUTENTICACION)
			.filter(e -> !e.isExitoso())
			.filter(e -> Objects.equals(e.getTableId(), actual.getTableId()))
			.filter(e -> e.getTimestamp() != null && !e.getTimestamp().isBefore(desde)
				&& !e.getTimestamp().isAfter(referencia))
			.count();

		if (!actual.isExitoso() && actual.getTipo() == TipoEventoVotacion.AUTENTICACION) {
			fallidos += 1;
		}

		if (fallidos < umbral) {
			return Optional.empty();
		}

		Severidad severidad = ParametrosRegla.calcularSeveridad(fallidos, umbral, regla.getRiskScoreWeight());
		Map<String, Object> detalles = new LinkedHashMap<>();
		detalles.put("intentosFallidos", fallidos);
		detalles.put("umbral", umbral);
		detalles.put("ventanaMinutos", ventanaMinutos);

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
