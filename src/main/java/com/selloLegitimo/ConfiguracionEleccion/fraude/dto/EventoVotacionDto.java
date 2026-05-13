package com.selloLegitimo.ConfiguracionEleccion.fraude.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.TipoEventoVotacion;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoVotacionDto {

	private String tableId;
	private String pollingStation;
	private String documentId;
	private TipoEventoVotacion tipo;
	private boolean exitoso;
	private boolean coincidenciaBiometrica;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime timestamp;

	private String detalles;
}
