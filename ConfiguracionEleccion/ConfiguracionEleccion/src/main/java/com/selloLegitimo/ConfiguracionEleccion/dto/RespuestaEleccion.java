package com.selloLegitimo.ConfiguracionEleccion.dto;

import com.selloLegitimo.ConfiguracionEleccion.modelo.EstadoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.ModalidadHabilitada;
import com.selloLegitimo.ConfiguracionEleccion.modelo.CodigoMetodoElectoral;
import com.selloLegitimo.ConfiguracionEleccion.modelo.ModeloCandidatura;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoCircunscripcion;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class RespuestaEleccion {

	private Long id;
	private String nombreOficial;
	private String pais;
	private TipoEleccion tipoEleccion;
	private CodigoMetodoElectoral codigoMetodoElectoral;
	private LocalDateTime fechaInicioJornada;
	private LocalDateTime fechaCierreJornada;
	private ModalidadHabilitada modalidadHabilitada;
	private TipoCircunscripcion tipoCircunscripcion;
	private String documentoNoVotable;
	private Integer numeroCurules;
	private String formulaCifraRepartidora;
	private String condicionVictoria;
	private EstadoEleccion estado;
	private List<ModeloCandidatura> modelosCandidatura;
	private List<String> excencionesHabilitadas;
	private ConfiguracionSenadoDto configuracionSenado;
	private List<ConfiguracionCamaraDeptoDto> configuracionCamara;
	private List<ConfiguracionCamaraEspecialDto> configuracionCamaraEspeciales;
}