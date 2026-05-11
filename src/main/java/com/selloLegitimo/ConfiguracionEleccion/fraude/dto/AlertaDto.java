package com.selloLegitimo.ConfiguracionEleccion.fraude.dto;

import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.Severidad;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaDto {

	private String alertType;
	private Long ruleId;
	private Severidad severity;
	private Map<String, Object> details;
	private LocalDateTime timestamp;
	private String tableId;
	private String documentId;

	public Map<String, Object> getDetails() {
		if (details == null) {
			details = new HashMap<>();
		}
		return details;
	}
}
