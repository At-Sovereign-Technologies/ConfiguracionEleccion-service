package com.selloLegitimo.ConfiguracionEleccion.fraude.evaluador;

import com.selloLegitimo.ConfiguracionEleccion.excepcion.ExcepcionReglaNegocio;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.Severidad;
import java.util.Map;

/**
 * Helper para extraer y validar parametros configurables de una regla.
 * Los parametros provienen del campo JSON 'parameters' almacenado en BD y no
 * se hardcodean en ningun evaluador.
 */
public final class ParametrosRegla {

	private ParametrosRegla() {
	}

	public static int obtenerEntero(Map<String, Object> params, String clave) {
		Object valor = obtenerObligatorio(params, clave);
		if (valor instanceof Number) {
			return ((Number) valor).intValue();
		}
		try {
			return Integer.parseInt(valor.toString().trim());
		} catch (NumberFormatException e) {
			throw new ExcepcionReglaNegocio("El parametro '" + clave + "' debe ser un entero");
		}
	}

	public static double obtenerDecimal(Map<String, Object> params, String clave) {
		Object valor = obtenerObligatorio(params, clave);
		if (valor instanceof Number) {
			return ((Number) valor).doubleValue();
		}
		try {
			return Double.parseDouble(valor.toString().trim());
		} catch (NumberFormatException e) {
			throw new ExcepcionReglaNegocio("El parametro '" + clave + "' debe ser numerico");
		}
	}

	public static int obtenerEnteroOPorDefecto(Map<String, Object> params, String clave, int valorPorDefecto) {
		if (params == null || !params.containsKey(clave) || params.get(clave) == null) {
			return valorPorDefecto;
		}
		return obtenerEntero(params, clave);
	}

	private static Object obtenerObligatorio(Map<String, Object> params, String clave) {
		if (params == null || !params.containsKey(clave) || params.get(clave) == null) {
			throw new ExcepcionReglaNegocio("Falta el parametro requerido '" + clave + "'");
		}
		return params.get(clave);
	}

	/**
	 * Calcula la severidad en funcion de cuanto excede el valor observado al umbral
	 * y del peso de la regla. Si la relacion observado/umbral es >= 3 la severidad
	 * sera CRITICAL aunque el peso de la regla sea bajo.
	 */
	public static Severidad calcularSeveridad(double observado, double umbral, int riskScoreWeight) {
		double razon = umbral <= 0 ? observado : observado / umbral;
		if (razon >= 3.0 || riskScoreWeight >= 80) {
			return Severidad.CRITICAL;
		}
		if (razon >= 2.0 || riskScoreWeight >= 60) {
			return Severidad.HIGH;
		}
		if (razon >= 1.5 || riskScoreWeight >= 30) {
			return Severidad.MEDIUM;
		}
		return Severidad.LOW;
	}
}
