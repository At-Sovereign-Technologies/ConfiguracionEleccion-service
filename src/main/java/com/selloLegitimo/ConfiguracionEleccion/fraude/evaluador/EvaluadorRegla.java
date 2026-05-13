package com.selloLegitimo.ConfiguracionEleccion.fraude.evaluador;

import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.AlertaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.VotingEventContext;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.ReglaAntifraude;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.TipoRegla;
import java.util.Optional;

public interface EvaluadorRegla {

	TipoRegla getTipoRegla();

	/**
	 * Evalua la regla contra el contexto del evento. Retorna una alerta tipificada si
	 * los parametros de la regla se cumplen; vacio en caso contrario.
	 */
	Optional<AlertaDto> evaluar(ReglaAntifraude regla, VotingEventContext context);
}
