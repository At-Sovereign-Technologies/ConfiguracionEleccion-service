package com.selloLegitimo.ConfiguracionEleccion.fraude.servicio;

import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.RespuestaReglaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.SolicitudCrearReglaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.SolicitudEditarReglaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.seguridad.UsuarioContexto;
import java.util.List;

public interface IServicioReglasAntifraude {

	List<RespuestaReglaDto> listar();

	RespuestaReglaDto obtenerPorId(Long id);

	RespuestaReglaDto crear(SolicitudCrearReglaDto solicitud, UsuarioContexto usuario);

	RespuestaReglaDto editar(Long id, SolicitudEditarReglaDto solicitud, UsuarioContexto usuario);

	RespuestaReglaDto aprobar(Long id, UsuarioContexto usuario);

	RespuestaReglaDto rechazar(Long id, String motivo, UsuarioContexto usuario);

	RespuestaReglaDto desactivar(Long id, UsuarioContexto usuario);
}
