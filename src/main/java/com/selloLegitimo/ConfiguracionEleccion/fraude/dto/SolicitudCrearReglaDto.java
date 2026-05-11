package com.selloLegitimo.ConfiguracionEleccion.fraude.dto;

import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.TipoRegla;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SolicitudCrearReglaDto {

	@NotBlank(message = "El nombre de la regla es obligatorio")
	@Size(max = 150, message = "El nombre no puede superar 150 caracteres")
	private String name;

	@Size(max = 500, message = "La descripcion no puede superar 500 caracteres")
	private String description;

	@NotNull(message = "El tipo de regla es obligatorio")
	private TipoRegla ruleType;

	@NotNull(message = "El estado activo es obligatorio")
	private Boolean isActive;

	@NotNull(message = "Los parametros son obligatorios")
	private Map<String, Object> parameters;

	@NotBlank(message = "El tipo de alerta es obligatorio")
	@Size(max = 80, message = "El tipo de alerta no puede superar 80 caracteres")
	private String alertType;

	@NotNull(message = "El peso de la regla es obligatorio")
	@Min(value = 0, message = "El peso debe ser >= 0")
	@Max(value = 100, message = "El peso debe ser <= 100")
	private Integer riskScoreWeight;
}
