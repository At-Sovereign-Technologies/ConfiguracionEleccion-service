package com.selloLegitimo.ConfiguracionEleccion.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.selloLegitimo.ConfiguracionEleccion.modelo.EstadoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.ModalidadHabilitada;
import com.selloLegitimo.ConfiguracionEleccion.modelo.CodigoMetodoElectoral;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoCircunscripcion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SolicitudCrearEleccion {

	@NotBlank(message = "El nombre oficial es obligatorio")
	@Size(max = 150, message = "El nombre oficial no puede superar 150 caracteres")
	private String nombreOficial;

	@NotBlank(message = "El pais es obligatorio")
	@Size(max = 100, message = "El pais no puede superar 100 caracteres")
	private String pais;

	@NotNull(message = "El tipo de eleccion es obligatorio")
	private TipoEleccion tipoEleccion;

	@NotNull(message = "El metodo electoral es obligatorio")
	private CodigoMetodoElectoral codigoMetodoElectoral;

	@NotNull(message = "La fecha de inicio es obligatoria")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime fechaInicioJornada;

	@NotNull(message = "La fecha de cierre es obligatoria")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime fechaCierreJornada;

	@NotNull(message = "La modalidad habilitada es obligatoria")
	private ModalidadHabilitada modalidadHabilitada;

	@NotNull(message = "El tipo de circunscripcion es obligatorio")
	private TipoCircunscripcion tipoCircunscripcion;

	@NotBlank(message = "El documento no votable es obligatorio")
	private String documentoNoVotable;

	private Integer numeroCurules;

	private String formulaCifraRepartidora;

	private String condicionVictoria;

	private EstadoEleccion estado;
}