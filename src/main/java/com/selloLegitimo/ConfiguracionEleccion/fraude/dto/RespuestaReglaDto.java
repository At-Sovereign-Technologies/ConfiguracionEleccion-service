package com.selloLegitimo.ConfiguracionEleccion.fraude.dto;

import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.EstadoAprobacion;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.TipoRegla;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RespuestaReglaDto {

	private Long id;
	private String name;
	private String description;
	private TipoRegla ruleType;
	private Boolean isActive;
	private Map<String, Object> parameters;
	private String alertType;
	private Integer riskScoreWeight;
	private EstadoAprobacion approvalStatus;
	private String createdBy;
	private String approvedBy;
	private String rejectionReason;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime approvedAt;
	private LocalDateTime deletedAt;
}
