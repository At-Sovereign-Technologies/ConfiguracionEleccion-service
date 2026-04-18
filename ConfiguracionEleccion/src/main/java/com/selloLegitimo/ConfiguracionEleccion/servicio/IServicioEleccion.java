package com.selloLegitimo.ConfiguracionEleccion.servicio;

import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaEleccion;
import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaModeloCandidatura;
import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaReglasVictoria;
import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaTarjeton;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudGenerarTarjeton;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudCrearEleccion;

public interface IServicioEleccion {

	RespuestaEleccion crearEleccion(SolicitudCrearEleccion solicitud);

	RespuestaEleccion obtenerEleccionPorId(Long id);

	RespuestaModeloCandidatura obtenerModeloCandidatura(Long idEleccion);

	RespuestaTarjeton generarTarjeton(Long idEleccion, SolicitudGenerarTarjeton solicitud);

	RespuestaReglasVictoria obtenerReglasVictoria(Long idEleccion);
}