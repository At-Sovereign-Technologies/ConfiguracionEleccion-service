package com.selloLegitimo.ConfiguracionEleccion.fraude.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SolicitudEditarReglaDto {

	@Size(max = 500, message = "La descripcion no puede superar 500 caracteres")
	private String description;

	private Boolean isActive;

	// Parametros completos a aplicar (reemplaza el contenido anterior si se provee).
	private Map<String, Object> parameters;

	@Size(max = 80, message = "El tipo de alerta no puede superar 80 caracteres")
	private String alertType;

	@Min(value = 0, message = "El peso debe ser >= 0")
	@Max(value = 100, message = "El peso debe ser <= 100")
	private Integer riskScoreWeight;
}
