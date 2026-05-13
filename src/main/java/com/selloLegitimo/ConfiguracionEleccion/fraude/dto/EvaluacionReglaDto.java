package com.selloLegitimo.ConfiguracionEleccion.fraude.dto;

import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.TipoRegla;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionReglaDto {

	private Long ruleId;
	private String ruleName;
	private TipoRegla ruleType;
	private boolean disparada;
	private Integer puntajeAportado;
	private String mensaje;
}
