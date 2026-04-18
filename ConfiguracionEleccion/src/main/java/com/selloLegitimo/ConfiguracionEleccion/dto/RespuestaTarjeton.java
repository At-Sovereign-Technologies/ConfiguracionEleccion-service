package com.selloLegitimo.ConfiguracionEleccion.dto;

import com.selloLegitimo.ConfiguracionEleccion.modelo.CodigoMetodoElectoral;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoTarjeton;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RespuestaTarjeton {

	private Long eleccionId;
	private String nombreEleccion;
	private CodigoMetodoElectoral metodoElectoral;
	private TipoTarjeton tipoTarjeton;
	private String idioma;
	private String circunscripcion;
	private Boolean incluyeVotoEnBlanco;
	private Boolean soporteMultiidioma;
	private Boolean requiereRankingNumerado;
	private List<OpcionTarjetonRespuesta> opciones;
}