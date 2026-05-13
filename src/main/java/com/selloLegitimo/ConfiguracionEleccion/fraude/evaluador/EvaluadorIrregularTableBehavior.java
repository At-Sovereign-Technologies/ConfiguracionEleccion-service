package com.selloLegitimo.ConfiguracionEleccion.fraude.evaluador;

import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.AlertaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.EventoVotacionDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.VotingEventContext;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.ReglaAntifraude;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.Severidad;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.TipoEventoVotacion;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.TipoRegla;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class EvaluadorIrregularTableBehavior implements EvaluadorRegla {

	@Override
	public TipoRegla getTipoRegla() {
		return TipoRegla.IRREGULAR_TABLE_BEHAVIOR;
	}

	@Override
	public Optional<AlertaDto> evaluar(ReglaAntifraude regla, VotingEventContext context) {
		Map<String, Object> params = regla.getParameters();
		double factorDesviacion = ParametrosRegla.obtenerDecimal(params, "deviationThreshold");
		int minMesasComparables = ParametrosRegla.obtenerEntero(params, "minComparableTables");

		EventoVotacionDto actual = context.getEventoActual();
		Map<String, List<EventoVotacionDto>> porMesa = context.getEventosPorMesaDelPuesto();
		if (porMesa == null || porMesa.isEmpty() || actual.getTableId() == null) {
			return Optional.empty();
		}

		long autenticacionesMesaActual = porMesa.getOrDefault(actual.getTableId(), List.of()).stream()
			.filter(e -> e.getTipo() == TipoEventoVotacion.AUTENTICACION)
			.count();

		long[] volumenComparables = porMesa.entrySet().stream()
			.filter(en -> !Objects.equals(en.getKey(), actual.getTableId()))
			.mapToLong(en -> en.getValue().stream()
				.filter(e -> e.getTipo() == TipoEventoVotacion.AUTENTICACION)
				.count())
			.toArray();

		if (volumenComparables.length < minMesasComparables) {
			return Optional.empty();
		}

		double promedio = 0;
		for (long v : volumenComparables) {
			promedio += v;
		}
		promedio = promedio / volumenComparables.length;

		double umbral = promedio * factorDesviacion;
		if (autenticacionesMesaActual <= umbral) {
			return Optional.empty();
		}

		Severidad severidad = ParametrosRegla.calcularSeveridad(autenticacionesMesaActual, umbral, regla.getRiskScoreWeight());
		Map<String, Object> detalles = new LinkedHashMap<>();
		detalles.put("autenticacionesMesa", autenticacionesMesaActual);
		detalles.put("promedioComparables", promedio);
		detalles.put("factorDesviacion", factorDesviacion);
		detalles.put("mesasComparables", volumenComparables.length);

		return Optional.of(AlertaDto.builder()
			.alertType(regla.getAlertType())
			.ruleId(regla.getId())
			.severity(severidad)
			.details(detalles)
			.timestamp(actual.getTimestamp())
			.tableId(actual.getTableId())
			.documentId(actual.getDocumentId())
			.build());
	}
}
