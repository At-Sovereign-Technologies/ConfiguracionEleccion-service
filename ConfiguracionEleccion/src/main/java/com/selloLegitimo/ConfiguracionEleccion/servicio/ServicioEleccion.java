package com.selloLegitimo.ConfiguracionEleccion.servicio;

import com.selloLegitimo.ConfiguracionEleccion.dto.OpcionTarjetonRespuesta;
import com.selloLegitimo.ConfiguracionEleccion.dto.OpcionTarjetonSolicitud;
import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaEleccion;
import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaModeloCandidatura;
import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaReglasVictoria;
import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaTarjeton;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudGenerarTarjeton;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudCrearEleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.CodigoMetodoElectoral;
import com.selloLegitimo.ConfiguracionEleccion.excepcion.ExcepcionRecursoNoEncontrado;
import com.selloLegitimo.ConfiguracionEleccion.excepcion.ExcepcionReglaNegocio;
import com.selloLegitimo.ConfiguracionEleccion.modelo.Eleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.EstadoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoCircunscripcion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoLista;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoModeloCandidatura;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoOpcionTarjeton;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoTarjeton;
import com.selloLegitimo.ConfiguracionEleccion.repositorio.RepositorioEleccion;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ServicioEleccion implements IServicioEleccion {

	private static final Logger logger = LoggerFactory.getLogger(ServicioEleccion.class);
	private static final String SEPARADOR_LISTA = "|";
	private static final String FORMULA_CIFRA_REPARTIDORA_DEFECTO = "D_HONDT";
	private static final String CRITERIO_ELIMINACION_DEFECTO = "MENOR_NUMERO_DE_PRIMERAS_PREFERENCIAS";
	private static final String CONDICION_VICTORIA_AV_DEFECTO = "MAYORIA_ABSOLUTA_SOBRE_PRIMERAS_PREFERENCIAS_ACTIVAS";
	private static final Set<String> CIRCUNSCRIPCIONES_ESPECIALES_VALIDAS = Set.of(
		"INDIGENA", "AFRODESCENDIENTE", "EXTERIOR", "PAZ");

	@Autowired
	private RepositorioEleccion repositorioEleccion;

	@Override
	@Transactional
	public RespuestaEleccion crearEleccion(SolicitudCrearEleccion solicitud) {
		logger.info("Iniciando creacion de eleccion con nombre {} para el pais {}",
			solicitud.getNombreOficial(), solicitud.getPais());

		validarConfiguracionBasica(solicitud);
		validarFechas(solicitud);
		List<String> jerarquiaNormalizada = normalizarJerarquia(solicitud.getJerarquiaGeografica());
		List<String> circunscripcionesEspeciales = normalizarCircunscripcionesEspeciales(solicitud.getCircunscripcionesEspeciales());

		Eleccion eleccion = new Eleccion();
		eleccion.setNombreOficial(solicitud.getNombreOficial().trim());
		eleccion.setPais(solicitud.getPais().trim());
		eleccion.setTipoEleccion(solicitud.getTipoEleccion());
		eleccion.setCodigoMetodoElectoral(solicitud.getCodigoMetodoElectoral());
		eleccion.setFechaInicioJornada(solicitud.getFechaInicioJornada());
		eleccion.setFechaCierreJornada(solicitud.getFechaCierreJornada());
		eleccion.setModalidadHabilitada(solicitud.getModalidadHabilitada());
		eleccion.setTipoCircunscripcion(solicitud.getTipoCircunscripcion());
		eleccion.setJerarquiaGeografica(convertirListaATexto(jerarquiaNormalizada));
		eleccion.setCircunscripcionesEspeciales(convertirListaATexto(circunscripcionesEspeciales));
		eleccion.setZonaHoraria(solicitud.getZonaHoraria().trim());
		eleccion.setIdioma(solicitud.getIdioma().trim());
		eleccion.setDocumentoIdentidadValido(solicitud.getDocumentoIdentidadValido().trim());
		eleccion.setReglasElegibilidad(solicitud.getReglasElegibilidad().trim());
		aplicarReglasVictoria(eleccion, solicitud);
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

	@Override
	@Transactional(readOnly = true)
	public RespuestaModeloCandidatura obtenerModeloCandidatura(Long idEleccion) {
		Eleccion eleccion = repositorioEleccion.findById(idEleccion)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("No existe una eleccion con id " + idEleccion));

		logger.info("Obteniendo modelo de candidatura para la eleccion {} con metodo {}", idEleccion,
			eleccion.getCodigoMetodoElectoral());

		return construirModeloCandidatura(eleccion);
	}

	@Override
	@Transactional(readOnly = true)
	public RespuestaTarjeton generarTarjeton(Long idEleccion, SolicitudGenerarTarjeton solicitud) {
		Eleccion eleccion = repositorioEleccion.findById(idEleccion)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("No existe una eleccion con id " + idEleccion));

		String circunscripcion = solicitud.getCircunscripcion().trim();
		validarCircunscripcionTarjeton(eleccion, circunscripcion);
		List<OpcionTarjetonRespuesta> opciones = construirOpcionesTarjeton(eleccion, circunscripcion, solicitud.getOpcionesValidadas());

		RespuestaTarjeton respuesta = new RespuestaTarjeton();
		respuesta.setEleccionId(eleccion.getId());
		respuesta.setNombreEleccion(eleccion.getNombreOficial());
		respuesta.setMetodoElectoral(eleccion.getCodigoMetodoElectoral());
		respuesta.setTipoTarjeton(obtenerTipoTarjeton(eleccion.getCodigoMetodoElectoral()));
		respuesta.setIdioma(eleccion.getIdioma());
		respuesta.setCircunscripcion(circunscripcion);
		respuesta.setIncluyeVotoEnBlanco(true);
		respuesta.setSoporteMultiidioma(true);
		respuesta.setRequiereRankingNumerado(eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_04);
		respuesta.setOpciones(opciones);
		return respuesta;
	}

	@Override
	@Transactional(readOnly = true)
	public RespuestaReglasVictoria obtenerReglasVictoria(Long idEleccion) {
		Eleccion eleccion = repositorioEleccion.findById(idEleccion)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("No existe una eleccion con id " + idEleccion));

		RespuestaReglasVictoria respuesta = new RespuestaReglasVictoria();
		respuesta.setEleccionId(eleccion.getId());
		respuesta.setMetodoElectoral(eleccion.getCodigoMetodoElectoral());
		respuesta.setUmbralPrimeraVueltaPorcentaje(eleccion.getUmbralPrimeraVueltaPorcentaje());
		respuesta.setRequiereMasUnoPrimeraVuelta(eleccion.getRequiereMasUnoPrimeraVuelta());
		respuesta.setPorcentajeUmbralListas(eleccion.getPorcentajeUmbralListas());
		respuesta.setNumeroCurules(eleccion.getNumeroCurules());
		respuesta.setFormulaCifraRepartidora(eleccion.getFormulaCifraRepartidora());
		respuesta.setCriterioEliminacion(eleccion.getCriterioEliminacion());
		respuesta.setCondicionVictoria(eleccion.getCondicionVictoria());

		if (eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_01) {
			respuesta.setUsaUmbral(false);
			respuesta.setDescripcionRegla("ME-01 no usa umbral y gana quien obtenga mas votos validos en una sola vuelta");
			return respuesta;
		}

		if (eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_02) {
			respuesta.setUsaUmbral(true);
			respuesta.setDescripcionRegla("ME-02 usa umbral configurable de primera vuelta sobre votos validos");
			return respuesta;
		}

		if (eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_03) {
			respuesta.setUsaUmbral(true);
			respuesta.setDescripcionRegla("ME-03 aplica umbral porcentual, distribucion de curules y formula de cifra repartidora");
			return respuesta;
		}

		respuesta.setUsaUmbral(false);
		respuesta.setDescripcionRegla("ME-04 aplica eliminacion iterativa y victoria por mayoria sobre primeras preferencias activas");
		return respuesta;
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

		if (!StringUtils.hasText(solicitud.getZonaHoraria())) {
			throw new ExcepcionReglaNegocio("La zona horaria es obligatoria");
		}

		if (!StringUtils.hasText(solicitud.getIdioma())) {
			throw new ExcepcionReglaNegocio("El idioma es obligatorio");
		}

		if (!StringUtils.hasText(solicitud.getDocumentoIdentidadValido())) {
			throw new ExcepcionReglaNegocio("El documento de identidad valido es obligatorio");
		}

		if (!StringUtils.hasText(solicitud.getReglasElegibilidad())) {
			throw new ExcepcionReglaNegocio("Las reglas de elegibilidad son obligatorias");
		}

		validarConfiguracionReglasVictoria(solicitud);
	}

	private void validarConfiguracionReglasVictoria(SolicitudCrearEleccion solicitud) {
		if (solicitud.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_01) {
			return;
		}

		if (solicitud.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_02) {
			if (solicitud.getUmbralPrimeraVueltaPorcentaje() != null
				&& (solicitud.getUmbralPrimeraVueltaPorcentaje() <= 0 || solicitud.getUmbralPrimeraVueltaPorcentaje() >= 100)) {
				throw new ExcepcionReglaNegocio("El umbral de primera vuelta debe estar entre 0 y 100");
			}
			return;
		}

		if (solicitud.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_03) {
			if (solicitud.getPorcentajeUmbralListas() == null || solicitud.getPorcentajeUmbralListas() <= 0 || solicitud.getPorcentajeUmbralListas() >= 100) {
				throw new ExcepcionReglaNegocio("ME-03 exige un porcentaje de umbral valido");
			}

			if (solicitud.getNumeroCurules() == null || solicitud.getNumeroCurules() < 1) {
				throw new ExcepcionReglaNegocio("ME-03 exige un numero de curules valido");
			}

			return;
		}

		if (!StringUtils.hasText(solicitud.getCriterioEliminacion())) {
			// Se permite valor por defecto, no lanzar error.
		}
	}

	private List<String> normalizarJerarquia(List<String> jerarquiaGeografica) {
		if (jerarquiaGeografica == null || jerarquiaGeografica.isEmpty()) {
			throw new ExcepcionReglaNegocio("La jerarquia geografica es obligatoria");
		}

		List<String> jerarquiaNormalizada = jerarquiaGeografica.stream()
			.map(nivel -> nivel == null ? "" : nivel.trim())
			.toList();

		if (jerarquiaNormalizada.stream().anyMatch(nivel -> !StringUtils.hasText(nivel))) {
			throw new ExcepcionReglaNegocio("Todos los niveles de la jerarquia geografica deben tener valor");
		}

		return jerarquiaNormalizada;
	}

	private List<String> normalizarCircunscripcionesEspeciales(List<String> circunscripcionesEspeciales) {
		if (circunscripcionesEspeciales == null || circunscripcionesEspeciales.isEmpty()) {
			return List.of();
		}

		List<String> circunscripcionesNormalizadas = circunscripcionesEspeciales.stream()
			.map(valor -> valor == null ? "" : valor.trim().toUpperCase())
			.toList();

		if (circunscripcionesNormalizadas.stream().anyMatch(valor -> !StringUtils.hasText(valor))) {
			throw new ExcepcionReglaNegocio("Las circunscripciones especiales no pueden contener valores vacios");
		}

		boolean existeValorInvalido = circunscripcionesNormalizadas.stream()
			.anyMatch(valor -> !CIRCUNSCRIPCIONES_ESPECIALES_VALIDAS.contains(valor));

		if (existeValorInvalido) {
			throw new ExcepcionReglaNegocio("Las circunscripciones especiales permitidas son INDIGENA, AFRODESCENDIENTE, EXTERIOR y PAZ");
		}

		return circunscripcionesNormalizadas;
	}

	@Override
	@Transactional(readOnly = true)
	public java.util.List<RespuestaEleccion> listarElecciones() {
		logger.info("Consultando listado de elecciones");
		return repositorioEleccion.findAll().stream()
			.map(this::convertirARespuesta)
			.collect(java.util.stream.Collectors.toList());
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
		respuesta.setJerarquiaGeografica(convertirTextoALista(eleccion.getJerarquiaGeografica()));
		respuesta.setCircunscripcionesEspeciales(convertirTextoALista(eleccion.getCircunscripcionesEspeciales()));
		respuesta.setZonaHoraria(eleccion.getZonaHoraria());
		respuesta.setIdioma(eleccion.getIdioma());
		respuesta.setDocumentoIdentidadValido(eleccion.getDocumentoIdentidadValido());
		respuesta.setReglasElegibilidad(eleccion.getReglasElegibilidad());
		respuesta.setUmbralPrimeraVueltaPorcentaje(eleccion.getUmbralPrimeraVueltaPorcentaje());
		respuesta.setRequiereMasUnoPrimeraVuelta(eleccion.getRequiereMasUnoPrimeraVuelta());
		respuesta.setPorcentajeUmbralListas(eleccion.getPorcentajeUmbralListas());
		respuesta.setNumeroCurules(eleccion.getNumeroCurules());
		respuesta.setFormulaCifraRepartidora(eleccion.getFormulaCifraRepartidora());
		respuesta.setCriterioEliminacion(eleccion.getCriterioEliminacion());
		respuesta.setCondicionVictoria(eleccion.getCondicionVictoria());
		respuesta.setEstado(eleccion.getEstado());
		return respuesta;
	}

	private RespuestaModeloCandidatura construirModeloCandidatura(Eleccion eleccion) {
		RespuestaModeloCandidatura respuesta = new RespuestaModeloCandidatura();
		respuesta.setEleccionId(eleccion.getId());
		respuesta.setMetodoElectoral(eleccion.getCodigoMetodoElectoral());
		respuesta.setTipoCircunscripcion(eleccion.getTipoCircunscripcion());
		respuesta.setJerarquiaGeografica(convertirTextoALista(eleccion.getJerarquiaGeografica()));
		respuesta.setCircunscripcionesEspeciales(convertirTextoALista(eleccion.getCircunscripcionesEspeciales()));
		respuesta.setRestringeMultiplesCircunscripcionesTerritoriales(true);

		if (eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_01
			|| eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_02) {
			respuesta.setTipoModeloCandidatura(TipoModeloCandidatura.UNINOMINAL);
			respuesta.setPermiteFormulaVicepresidencial(true);
			respuesta.setFormulaVicepresidencialOpcional(true);
			respuesta.setUsaListasPartido(false);
			respuesta.setTiposListaPermitidos(List.of());
			respuesta.setPermiteVotoPreferente(false);
			respuesta.setUsaRankingPreferencias(false);
			respuesta.setDescripcionModelo("Candidato uninominal con formula vicepresidencial opcional");
			return respuesta;
		}

		if (eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_03) {
			respuesta.setTipoModeloCandidatura(TipoModeloCandidatura.LISTA);
			respuesta.setPermiteFormulaVicepresidencial(false);
			respuesta.setFormulaVicepresidencialOpcional(false);
			respuesta.setUsaListasPartido(true);
			respuesta.setTiposListaPermitidos(List.of(TipoLista.CERRADA, TipoLista.ABIERTA));
			respuesta.setPermiteVotoPreferente(true);
			respuesta.setUsaRankingPreferencias(false);
			respuesta.setDescripcionModelo("Listas de partido con soporte para lista cerrada y abierta");
			return respuesta;
		}

		respuesta.setTipoModeloCandidatura(TipoModeloCandidatura.RANKING_INDIVIDUAL);
		respuesta.setPermiteFormulaVicepresidencial(false);
		respuesta.setFormulaVicepresidencialOpcional(false);
		respuesta.setUsaListasPartido(false);
		respuesta.setTiposListaPermitidos(List.of());
		respuesta.setPermiteVotoPreferente(false);
		respuesta.setUsaRankingPreferencias(true);
		respuesta.setDescripcionModelo("Candidatos individuales para ranking de preferencias");
		return respuesta;
	}

	private String convertirListaATexto(List<String> valores) {
		if (valores == null || valores.isEmpty()) {
			return null;
		}

		return valores.stream()
			.map(String::trim)
			.collect(Collectors.joining(SEPARADOR_LISTA));
	}

	private List<String> convertirTextoALista(String valorTexto) {
		if (!StringUtils.hasText(valorTexto)) {
			return Collections.emptyList();
		}

		return Arrays.stream(valorTexto.split("\\|"))
			.map(String::trim)
			.toList();
	}

	private void aplicarReglasVictoria(Eleccion eleccion, SolicitudCrearEleccion solicitud) {
		if (eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_01) {
			eleccion.setUmbralPrimeraVueltaPorcentaje(null);
			eleccion.setRequiereMasUnoPrimeraVuelta(null);
			eleccion.setPorcentajeUmbralListas(null);
			eleccion.setNumeroCurules(null);
			eleccion.setFormulaCifraRepartidora(null);
			eleccion.setCriterioEliminacion(null);
			eleccion.setCondicionVictoria("GANA_EL_MAYOR_NUMERO_DE_VOTOS_VALIDOS");
			return;
		}

		if (eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_02) {
			eleccion.setUmbralPrimeraVueltaPorcentaje(
				solicitud.getUmbralPrimeraVueltaPorcentaje() == null ? 50.0 : solicitud.getUmbralPrimeraVueltaPorcentaje());
			eleccion.setRequiereMasUnoPrimeraVuelta(
				solicitud.getRequiereMasUnoPrimeraVuelta() == null ? true : solicitud.getRequiereMasUnoPrimeraVuelta());
			eleccion.setPorcentajeUmbralListas(null);
			eleccion.setNumeroCurules(null);
			eleccion.setFormulaCifraRepartidora(null);
			eleccion.setCriterioEliminacion(null);
			eleccion.setCondicionVictoria("SUPERA_UMBRAL_DE_PRIMERA_VUELTA_SOBRE_VOTOS_VALIDOS");
			return;
		}

		if (eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_03) {
			eleccion.setUmbralPrimeraVueltaPorcentaje(null);
			eleccion.setRequiereMasUnoPrimeraVuelta(null);
			eleccion.setPorcentajeUmbralListas(solicitud.getPorcentajeUmbralListas());
			eleccion.setNumeroCurules(solicitud.getNumeroCurules());
			eleccion.setFormulaCifraRepartidora(
				StringUtils.hasText(solicitud.getFormulaCifraRepartidora()) ? solicitud.getFormulaCifraRepartidora().trim() : FORMULA_CIFRA_REPARTIDORA_DEFECTO);
			eleccion.setCriterioEliminacion(null);
			eleccion.setCondicionVictoria("DISTRIBUCION_PROPORCIONAL_DE_CURULES_ENTRE_LISTAS_QUE_SUPERAN_UMBRAL");
			return;
		}

		eleccion.setUmbralPrimeraVueltaPorcentaje(null);
		eleccion.setRequiereMasUnoPrimeraVuelta(null);
		eleccion.setPorcentajeUmbralListas(null);
		eleccion.setNumeroCurules(null);
		eleccion.setFormulaCifraRepartidora(null);
		eleccion.setCriterioEliminacion(
			StringUtils.hasText(solicitud.getCriterioEliminacion()) ? solicitud.getCriterioEliminacion().trim() : CRITERIO_ELIMINACION_DEFECTO);
		eleccion.setCondicionVictoria(
			StringUtils.hasText(solicitud.getCondicionVictoria()) ? solicitud.getCondicionVictoria().trim() : CONDICION_VICTORIA_AV_DEFECTO);
	}

	private void validarCircunscripcionTarjeton(Eleccion eleccion, String circunscripcion) {
		if (eleccion.getTipoCircunscripcion() == TipoCircunscripcion.NACIONAL && !"NACIONAL".equalsIgnoreCase(circunscripcion)) {
			throw new ExcepcionReglaNegocio("La eleccion nacional solo admite la circunscripcion NACIONAL");
		}

		if (eleccion.getTipoCircunscripcion() == TipoCircunscripcion.ESPECIAL) {
			boolean existe = convertirTextoALista(eleccion.getCircunscripcionesEspeciales()).stream()
				.anyMatch(valor -> valor.equalsIgnoreCase(circunscripcion));
			if (!existe) {
				throw new ExcepcionReglaNegocio("La circunscripcion solicitada no pertenece a las circunscripciones especiales configuradas");
			}
		}
	}

	private List<OpcionTarjetonRespuesta> construirOpcionesTarjeton(Eleccion eleccion, String circunscripcion,
		List<OpcionTarjetonSolicitud> opcionesValidadas) {
		if (opcionesValidadas == null || opcionesValidadas.isEmpty()) {
			throw new ExcepcionReglaNegocio("Debe enviar al menos una opcion validada para generar el tarjeton");
		}

		List<OpcionTarjetonRespuesta> opciones = opcionesValidadas.stream()
			.map(opcion -> convertirOpcionTarjeton(eleccion, circunscripcion, opcion))
			.toList();

		OpcionTarjetonRespuesta votoEnBlanco = new OpcionTarjetonRespuesta();
		votoEnBlanco.setPosicion(opciones.size() + 1);
		votoEnBlanco.setCodigo("VOTO_BLANCO");
		votoEnBlanco.setTipoOpcion(TipoOpcionTarjeton.VOTO_EN_BLANCO);
		votoEnBlanco.setEtiquetaPrincipal(traducirVotoEnBlanco(eleccion.getIdioma()));
		votoEnBlanco.setEtiquetaSecundaria(null);
		votoEnBlanco.setRequiereNumeroRanking(eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_04);
		votoEnBlanco.setNumeroMinimoRanking(eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_04 ? 1 : null);
		votoEnBlanco.setNumeroMaximoRanking(eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_04 ? opciones.size() + 1 : null);

		return java.util.stream.Stream.concat(opciones.stream(), java.util.stream.Stream.of(votoEnBlanco)).toList();
	}

	private OpcionTarjetonRespuesta convertirOpcionTarjeton(Eleccion eleccion, String circunscripcion, OpcionTarjetonSolicitud opcion) {
		if (opcion == null || !Boolean.TRUE.equals(opcion.getValidada())) {
			throw new ExcepcionReglaNegocio("Todas las opciones del tarjeton deben venir validadas");
		}

		if (!StringUtils.hasText(opcion.getCircunscripcion()) || !opcion.getCircunscripcion().trim().equalsIgnoreCase(circunscripcion)) {
			throw new ExcepcionReglaNegocio("Todas las opciones deben pertenecer a la circunscripcion solicitada");
		}

		validarOpcionSegunMetodo(eleccion.getCodigoMetodoElectoral(), opcion);

		OpcionTarjetonRespuesta respuesta = new OpcionTarjetonRespuesta();
		respuesta.setPosicion(0);
		respuesta.setCodigo(StringUtils.hasText(opcion.getCodigo()) ? opcion.getCodigo().trim() : opcion.getNombreMostrado().trim());
		respuesta.setTipoOpcion(opcion.getTipoOpcion());
		respuesta.setEtiquetaPrincipal(opcion.getNombreMostrado().trim());
		respuesta.setEtiquetaSecundaria(StringUtils.hasText(opcion.getPartidoPolitico()) ? opcion.getPartidoPolitico().trim() : opcion.getNombreLista());
		if (eleccion.getCodigoMetodoElectoral() == CodigoMetodoElectoral.ME_04) {
			respuesta.setRequiereNumeroRanking(true);
			respuesta.setNumeroMinimoRanking(1);
			respuesta.setNumeroMaximoRanking(0);
		} else {
			respuesta.setRequiereNumeroRanking(false);
		}
		return respuesta;
	}

	private void validarOpcionSegunMetodo(CodigoMetodoElectoral metodo, OpcionTarjetonSolicitud opcion) {
		if (!StringUtils.hasText(opcion.getNombreMostrado())) {
			throw new ExcepcionReglaNegocio("Cada opcion del tarjeton debe tener un nombre mostrado");
		}

		if (metodo == CodigoMetodoElectoral.ME_01 || metodo == CodigoMetodoElectoral.ME_02 || metodo == CodigoMetodoElectoral.ME_04) {
			if (opcion.getTipoOpcion() != TipoOpcionTarjeton.CANDIDATO) {
				throw new ExcepcionReglaNegocio("El metodo electoral seleccionado exige opciones de tipo candidato");
			}
			return;
		}

		if (opcion.getTipoOpcion() != TipoOpcionTarjeton.LISTA) {
			throw new ExcepcionReglaNegocio("El metodo ME-03 exige opciones de tipo lista");
		}
	}

	private TipoTarjeton obtenerTipoTarjeton(CodigoMetodoElectoral metodo) {
		if (metodo == CodigoMetodoElectoral.ME_01 || metodo == CodigoMetodoElectoral.ME_02) {
			return TipoTarjeton.UNINOMINAL;
		}

		if (metodo == CodigoMetodoElectoral.ME_03) {
			return TipoTarjeton.PREFERENCIAL;
		}

		return TipoTarjeton.RANKING;
	}

	private String traducirVotoEnBlanco(String idioma) {
		if (idioma != null && idioma.toLowerCase().startsWith("en")) {
			return "Blank vote";
		}
		return "Voto en blanco";
	}
}