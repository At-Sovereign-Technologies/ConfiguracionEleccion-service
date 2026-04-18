package com.selloLegitimo.ConfiguracionEleccion.dto;

import com.selloLegitimo.ConfiguracionEleccion.modelo.CodigoMetodoElectoral;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoCircunscripcion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoLista;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoModeloCandidatura;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RespuestaModeloCandidatura {

	private Long eleccionId;
	private CodigoMetodoElectoral metodoElectoral;
	private TipoModeloCandidatura tipoModeloCandidatura;
	private Boolean permiteFormulaVicepresidencial;
	private Boolean formulaVicepresidencialOpcional;
	private Boolean usaListasPartido;
	private List<TipoLista> tiposListaPermitidos;
	private Boolean permiteVotoPreferente;
	private Boolean usaRankingPreferencias;
	private TipoCircunscripcion tipoCircunscripcion;
	private List<String> jerarquiaGeografica;
	private List<String> circunscripcionesEspeciales;
	private Boolean restringeMultiplesCircunscripcionesTerritoriales;
	private String descripcionModelo;
}