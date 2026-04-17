package com.selloLegitimo.ConfiguracionEleccion.servicio;

import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaEleccion;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudCrearEleccion;

public interface IServicioEleccion {

	RespuestaEleccion crearEleccion(SolicitudCrearEleccion solicitud);

	RespuestaEleccion obtenerEleccionPorId(Long id);
}