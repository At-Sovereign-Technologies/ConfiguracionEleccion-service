package com.selloLegitimo.ConfiguracionEleccion.dto;

import com.selloLegitimo.ConfiguracionEleccion.modelo.EstadoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.ModalidadHabilitada;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoEleccion;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RespuestaEleccion {

	private Long id;
	private String nombreOficial;
	private String pais;
	private TipoEleccion tipoEleccion;
	private LocalDateTime fechaInicioJornada;
	private LocalDateTime fechaCierreJornada;
	private ModalidadHabilitada modalidadHabilitada;
	private EstadoEleccion estado;
}