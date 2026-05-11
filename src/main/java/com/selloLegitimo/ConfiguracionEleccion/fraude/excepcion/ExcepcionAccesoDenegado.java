package com.selloLegitimo.ConfiguracionEleccion.fraude.excepcion;

public class ExcepcionAccesoDenegado extends RuntimeException {

	public ExcepcionAccesoDenegado(String mensaje) {
		super(mensaje);
	}
}
