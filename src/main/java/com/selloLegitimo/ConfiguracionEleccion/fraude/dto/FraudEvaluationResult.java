package com.selloLegitimo.ConfiguracionEleccion.fraude.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudEvaluationResult {

	private List<AlertaDto> alerts;
	private Integer totalRiskScore;
	private List<EvaluacionReglaDto> evaluatedRules;

	public List<AlertaDto> getAlerts() {
		if (alerts == null) {
			alerts = new ArrayList<>();
		}
		return alerts;
	}

	public List<EvaluacionReglaDto> getEvaluatedRules() {
		if (evaluatedRules == null) {
			evaluatedRules = new ArrayList<>();
		}
		return evaluatedRules;
	}
}
