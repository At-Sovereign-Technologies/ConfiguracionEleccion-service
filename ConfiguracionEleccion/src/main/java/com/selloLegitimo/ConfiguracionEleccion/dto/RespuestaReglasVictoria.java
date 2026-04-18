package com.selloLegitimo.ConfiguracionEleccion.dto;

import com.selloLegitimo.ConfiguracionEleccion.modelo.CodigoMetodoElectoral;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RespuestaReglasVictoria {

	private Long eleccionId;
	private CodigoMetodoElectoral metodoElectoral;
	private Boolean usaUmbral;
	private Double umbralPrimeraVueltaPorcentaje;
	private Boolean requiereMasUnoPrimeraVuelta;
	private Double porcentajeUmbralListas;
	private Integer numeroCurules;
	private String formulaCifraRepartidora;
	private String criterioEliminacion;
	private String condicionVictoria;
	private String descripcionRegla;
}