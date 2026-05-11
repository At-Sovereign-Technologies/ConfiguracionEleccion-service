package com.selloLegitimo.ConfiguracionEleccion.fraude.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotingEventContext {

	@NotNull(message = "El evento actual es obligatorio")
	@Valid
	private EventoVotacionDto eventoActual;

	// Eventos historicos relevantes (misma mesa, mismo documento o mismo puesto)
	// disponibles para la evaluacion de las reglas. Cada evaluador filtra lo que necesita.
	@Valid
	private List<EventoVotacionDto> eventosHistoricos;

	// Eventos historicos agrupados por mesa para reglas que comparan mesas dentro
	// de un mismo puesto (IRREGULAR_TABLE_BEHAVIOR). Opcional.
	private Map<String, List<EventoVotacionDto>> eventosPorMesaDelPuesto;

	public List<EventoVotacionDto> getEventosHistoricos() {
		if (eventosHistoricos == null) {
			return new ArrayList<>();
		}
		return eventosHistoricos;
	}
}
