package com.selloLegitimo.ConfiguracionEleccion.servicio;

import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaEleccion;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudCrearEleccion;
import com.selloLegitimo.ConfiguracionEleccion.excepcion.ExcepcionRecursoNoEncontrado;
import com.selloLegitimo.ConfiguracionEleccion.excepcion.ExcepcionReglaNegocio;
import com.selloLegitimo.ConfiguracionEleccion.modelo.Eleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.EstadoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.repositorio.RepositorioEleccion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioEleccion implements IServicioEleccion {

	private static final Logger logger = LoggerFactory.getLogger(ServicioEleccion.class);

	@Autowired
	private RepositorioEleccion repositorioEleccion;

	@Override
	@Transactional
	public RespuestaEleccion crearEleccion(SolicitudCrearEleccion solicitud) {
		logger.info("Iniciando creacion de eleccion con nombre {} para el pais {}",
			solicitud.getNombreOficial(), solicitud.getPais());

		validarFechas(solicitud);

		Eleccion eleccion = new Eleccion();
		eleccion.setNombreOficial(solicitud.getNombreOficial().trim());
		eleccion.setPais(solicitud.getPais().trim());
		eleccion.setTipoEleccion(solicitud.getTipoEleccion());
		eleccion.setFechaInicioJornada(solicitud.getFechaInicioJornada());
		eleccion.setFechaCierreJornada(solicitud.getFechaCierreJornada());
		eleccion.setModalidadHabilitada(solicitud.getModalidadHabilitada());
		eleccion.setEstado(solicitud.getEstado() == null ? EstadoEleccion.BORRADOR : solicitud.getEstado());

		Eleccion eleccionGuardada = repositorioEleccion.save(eleccion);
		logger.info("Eleccion creada exitosamente con id {} y estado {}", eleccionGuardada.getId(), eleccionGuardada.getEstado());

		return convertirARespuesta(eleccionGuardada);
	}

	@Override
	@Transactional(readOnly = true)
	public RespuestaEleccion obtenerEleccionPorId(Long id) {
		logger.info("Consultando eleccion con id {}", id);
		Eleccion eleccion = repositorioEleccion.findById(id)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("No existe una eleccion con id " + id));

		logger.info("Eleccion con id {} encontrada", id);
		return convertirARespuesta(eleccion);
	}

	private void validarFechas(SolicitudCrearEleccion solicitud) {
		if (solicitud.getFechaCierreJornada().isBefore(solicitud.getFechaInicioJornada())) {
			throw new ExcepcionReglaNegocio("La fecha de cierre no puede ser anterior a la fecha de inicio");
		}

		if (solicitud.getFechaCierreJornada().isEqual(solicitud.getFechaInicioJornada())) {
			throw new ExcepcionReglaNegocio("La fecha de cierre debe ser posterior a la fecha de inicio");
		}
	}

	private RespuestaEleccion convertirARespuesta(Eleccion eleccion) {
		RespuestaEleccion respuesta = new RespuestaEleccion();
		respuesta.setId(eleccion.getId());
		respuesta.setNombreOficial(eleccion.getNombreOficial());
		respuesta.setPais(eleccion.getPais());
		respuesta.setTipoEleccion(eleccion.getTipoEleccion());
		respuesta.setFechaInicioJornada(eleccion.getFechaInicioJornada());
		respuesta.setFechaCierreJornada(eleccion.getFechaCierreJornada());
		respuesta.setModalidadHabilitada(eleccion.getModalidadHabilitada());
		respuesta.setEstado(eleccion.getEstado());
		return respuesta;
	}
}