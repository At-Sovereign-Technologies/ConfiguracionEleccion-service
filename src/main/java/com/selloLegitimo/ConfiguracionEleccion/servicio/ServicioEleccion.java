package com.selloLegitimo.ConfiguracionEleccion.servicio;

import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaEleccion;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudCrearEleccion;
import com.selloLegitimo.ConfiguracionEleccion.dto.ConfiguracionSenadoDto;
import com.selloLegitimo.ConfiguracionEleccion.dto.ConfiguracionCamaraDeptoDto;
import com.selloLegitimo.ConfiguracionEleccion.dto.ConfiguracionCamaraEspecialDto;
import com.selloLegitimo.ConfiguracionEleccion.excepcion.ExcepcionRecursoNoEncontrado;
import com.selloLegitimo.ConfiguracionEleccion.excepcion.ExcepcionReglaNegocio;
import com.selloLegitimo.ConfiguracionEleccion.modelo.CodigoMetodoElectoral;
import com.selloLegitimo.ConfiguracionEleccion.modelo.Eleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.EstadoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.ModeloCandidatura;
import com.selloLegitimo.ConfiguracionEleccion.repositorio.RepositorioEleccion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
public class ServicioEleccion {

	private static final Logger logger = LoggerFactory.getLogger(ServicioEleccion.class);
	private static final String FORMULA_CIFRA_REPARTIDORA_DEFECTO = "D_HONDT";
	private static final String CONDICION_VICTORIA_AV_DEFECTO = "MAYORIA_ABSOLUTA_SOBRE_PRIMERAS_PREFERENCIAS_ACTIVAS";
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Autowired
	private RepositorioEleccion repositorioEleccion;

	@Transactional
	public RespuestaEleccion crearEleccion(SolicitudCrearEleccion solicitud) {
		logger.info("Iniciando creacion de eleccion con nombre {} para el pais {}",
			solicitud.getNombreOficial(), solicitud.getPais());

		validarConfiguracionBasica(solicitud);
		validarFechas(solicitud);

		Eleccion eleccion = new Eleccion();
		eleccion.setNombreOficial(solicitud.getNombreOficial().trim());
		eleccion.setPais(solicitud.getPais().trim());
		eleccion.setTipoEleccion(solicitud.getTipoEleccion());
		eleccion.setCodigoMetodoElectoral(solicitud.getCodigoMetodoElectoral());
		eleccion.setFechaInicioJornada(solicitud.getFechaInicioJornada());
		eleccion.setFechaCierreJornada(solicitud.getFechaCierreJornada());
		eleccion.setModalidadHabilitada(solicitud.getModalidadHabilitada());
		eleccion.setTipoCircunscripcion(solicitud.getTipoCircunscripcion());
		eleccion.setDocumentoNoVotable(solicitud.getDocumentoNoVotable().trim());
		aplicarConfiguracionMetodo(eleccion, solicitud);
		eleccion.setModelosCandidatura(solicitud.getModelosCandidatura());
		eleccion.setExcencionesHabilitadas(solicitud.getExcencionesHabilitadas());
		persistirConfiguracionLegislativa(eleccion, solicitud);
		eleccion.setEstado(solicitud.getEstado() == null ? EstadoEleccion.BORRADOR : solicitud.getEstado());

		Eleccion eleccionGuardada = repositorioEleccion.save(eleccion);
		logger.info("Eleccion creada exitosamente con id {} y estado {}", eleccionGuardada.getId(), eleccionGuardada.getEstado());
		return convertirARespuesta(eleccionGuardada);
	}

	@Transactional(readOnly = true)
	public RespuestaEleccion obtenerEleccionPorId(Long id) {
		logger.info("Consultando eleccion con id {}", id);
		Eleccion eleccion = repositorioEleccion.findById(id)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("No existe una eleccion con id " + id));
		return convertirARespuesta(eleccion);
	}

	@Transactional(readOnly = true)
	public List<RespuestaEleccion> listarElecciones() {
		logger.info("Consultando listado de elecciones");
		return repositorioEleccion.findAll().stream()
			.map(this::convertirARespuesta)
			.toList();
	}

	private void validarFechas(SolicitudCrearEleccion solicitud) {
		if (solicitud.getFechaCierreJornada().isBefore(solicitud.getFechaInicioJornada())) {
			throw new ExcepcionReglaNegocio("La fecha de cierre no puede ser anterior a la fecha de inicio");
		}

		if (solicitud.getFechaCierreJornada().isEqual(solicitud.getFechaInicioJornada())) {
			throw new ExcepcionReglaNegocio("La fecha de cierre debe ser posterior a la fecha de inicio");
		}
	}

	private void validarConfiguracionBasica(SolicitudCrearEleccion solicitud) {
		if (solicitud.getCodigoMetodoElectoral() == null) {
			throw new ExcepcionReglaNegocio("El metodo electoral es obligatorio");
		}

		if (solicitud.getTipoCircunscripcion() == null) {
			throw new ExcepcionReglaNegocio("El tipo de circunscripcion es obligatorio");
		}

		if (solicitud.getModalidadHabilitada() == null) {
			throw new ExcepcionReglaNegocio("La modalidad habilitada es obligatoria");
		}

		if (!StringUtils.hasText(solicitud.getDocumentoNoVotable())) {
			throw new ExcepcionReglaNegocio("El documento no votable es obligatorio");
		}

		validarCamposPorMetodo(solicitud);
	}

	private void validarCamposPorMetodo(SolicitudCrearEleccion solicitud) {
		if (solicitud.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_03) {
			if (solicitud.getNumeroCurules() == null || solicitud.getNumeroCurules() < 1) {
				throw new ExcepcionReglaNegocio("ME-03 exige un numero de curules valido");
			}
			List<ModeloCandidatura> modelos = solicitud.getModelosCandidatura();
			if (modelos != null && !modelos.isEmpty()) {
				boolean contieneUnico = modelos.contains(ModeloCandidatura.UNICO);
				if (contieneUnico) {
					throw new ExcepcionReglaNegocio(
						"El metodo proporcional (ME-03) no admite candidato unico. Use lista abierta, cerrada o ambas.");
				}
			}
		} else {
			List<ModeloCandidatura> modelos = solicitud.getModelosCandidatura();
			if (modelos != null && !modelos.isEmpty()) {
				boolean soloListas = modelos.stream()
					.anyMatch(m -> m == ModeloCandidatura.ABIERTA || m == ModeloCandidatura.CERRADA);
				if (soloListas) {
					throw new ExcepcionReglaNegocio(
						"Las listas de partido (abierta/cerrada) solo aplican para el metodo proporcional (ME-03).");
				}
			}
		}
	}

	private void aplicarConfiguracionMetodo(Eleccion eleccion, SolicitudCrearEleccion solicitud) {
		if (eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_01) {
			eleccion.setNumeroCurules(null);
			eleccion.setFormulaCifraRepartidora(null);
			eleccion.setCondicionVictoria("GANA_EL_MAYOR_NUMERO_DE_VOTOS_VALIDOS");
			return;
		}

		if (eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_02) {
			eleccion.setNumeroCurules(null);
			eleccion.setFormulaCifraRepartidora(null);
			eleccion.setCondicionVictoria("SUPERA_UMBRAL_DE_PRIMERA_VUELTA_SOBRE_VOTOS_VALIDOS");
			return;
		}

		if (eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_03) {
			eleccion.setNumeroCurules(solicitud.getNumeroCurules());
			eleccion.setFormulaCifraRepartidora(
				StringUtils.hasText(solicitud.getFormulaCifraRepartidora())
					? solicitud.getFormulaCifraRepartidora().trim()
					: FORMULA_CIFRA_REPARTIDORA_DEFECTO);
			eleccion.setCondicionVictoria("DISTRIBUCION_PROPORCIONAL_DE_CURULES_ENTRE_LISTAS_QUE_SUPERAN_UMBRAL");
			return;
		}

		eleccion.setNumeroCurules(null);
		eleccion.setFormulaCifraRepartidora(null);
		eleccion.setCondicionVictoria(
			StringUtils.hasText(solicitud.getCondicionVictoria())
				? solicitud.getCondicionVictoria().trim()
				: CONDICION_VICTORIA_AV_DEFECTO);
	}

	private RespuestaEleccion convertirARespuesta(Eleccion eleccion) {
		RespuestaEleccion respuesta = new RespuestaEleccion();
		respuesta.setId(eleccion.getId());
		respuesta.setNombreOficial(eleccion.getNombreOficial());
		respuesta.setPais(eleccion.getPais());
		respuesta.setTipoEleccion(eleccion.getTipoEleccion());
		respuesta.setCodigoMetodoElectoral(eleccion.getCodigoMetodoElectoral());
		respuesta.setFechaInicioJornada(eleccion.getFechaInicioJornada());
		respuesta.setFechaCierreJornada(eleccion.getFechaCierreJornada());
		respuesta.setModalidadHabilitada(eleccion.getModalidadHabilitada());
		respuesta.setTipoCircunscripcion(eleccion.getTipoCircunscripcion());
		respuesta.setDocumentoNoVotable(eleccion.getDocumentoNoVotable());
		respuesta.setNumeroCurules(eleccion.getNumeroCurules());
		respuesta.setFormulaCifraRepartidora(eleccion.getFormulaCifraRepartidora());
		respuesta.setCondicionVictoria(eleccion.getCondicionVictoria());
		respuesta.setEstado(eleccion.getEstado());
		respuesta.setModelosCandidatura(eleccion.getModelosCandidatura());
		respuesta.setExcencionesHabilitadas(eleccion.getExcencionesHabilitadas());
		recuperarConfiguracionLegislativa(respuesta, eleccion);
		return respuesta;
	}

	private void persistirConfiguracionLegislativa(Eleccion eleccion, SolicitudCrearEleccion solicitud) {
		if (solicitud.getConfiguracionSenado() != null) {
			try {
				eleccion.setSenadoConfigJson(MAPPER.writeValueAsString(solicitud.getConfiguracionSenado()));
			} catch (Exception e) {
				logger.warn("No se pudo serializar configuracionSenado", e);
			}
		}
		if (solicitud.getConfiguracionCamara() != null && !solicitud.getConfiguracionCamara().isEmpty()) {
			try {
				eleccion.setCamaraDeptosjson(MAPPER.writeValueAsString(solicitud.getConfiguracionCamara()));
			} catch (Exception e) {
				logger.warn("No se pudo serializar configuracionCamara", e);
			}
		}
		if (solicitud.getConfiguracionCamaraEspeciales() != null && !solicitud.getConfiguracionCamaraEspeciales().isEmpty()) {
			try {
				eleccion.setCamaraEspecialesJson(MAPPER.writeValueAsString(solicitud.getConfiguracionCamaraEspeciales()));
			} catch (Exception e) {
				logger.warn("No se pudo serializar configuracionCamaraEspeciales", e);
			}
		}
	}

	private void recuperarConfiguracionLegislativa(RespuestaEleccion respuesta, Eleccion eleccion) {
		if (eleccion.getSenadoConfigJson() != null) {
			try {
				respuesta.setConfiguracionSenado(MAPPER.readValue(eleccion.getSenadoConfigJson(), ConfiguracionSenadoDto.class));
			} catch (Exception e) {
				logger.warn("No se pudo deserializar senadoConfigJson", e);
			}
		}
		if (eleccion.getCamaraDeptosjson() != null) {
			try {
				respuesta.setConfiguracionCamara(MAPPER.readValue(eleccion.getCamaraDeptosjson(),
					new TypeReference<List<ConfiguracionCamaraDeptoDto>>() {}));
			} catch (Exception e) {
				logger.warn("No se pudo deserializar camaraDeptosjson", e);
			}
		}
		if (eleccion.getCamaraEspecialesJson() != null) {
			try {
				respuesta.setConfiguracionCamaraEspeciales(MAPPER.readValue(eleccion.getCamaraEspecialesJson(),
					new TypeReference<List<ConfiguracionCamaraEspecialDto>>() {}));
			} catch (Exception e) {
				logger.warn("No se pudo deserializar camaraEspecialesJson", e);
			}
		}
	}
}