package com.selloLegitimo.ConfiguracionEleccion.dto;

import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoOpcionTarjeton;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpcionTarjetonRespuesta {

	private Integer posicion;
	private String codigo;
	private TipoOpcionTarjeton tipoOpcion;
	private String etiquetaPrincipal;
	private String etiquetaSecundaria;
	private Boolean requiereNumeroRanking;
	private Integer numeroMinimoRanking;
	private Integer numeroMaximoRanking;
}