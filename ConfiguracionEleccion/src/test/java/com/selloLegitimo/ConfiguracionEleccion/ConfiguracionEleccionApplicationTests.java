package com.selloLegitimo.ConfiguracionEleccion;

import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaEleccion;
import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaModeloCandidatura;
import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaReglasVictoria;
import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaTarjeton;
import com.selloLegitimo.ConfiguracionEleccion.dto.OpcionTarjetonSolicitud;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudGenerarTarjeton;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudCrearEleccion;
import com.selloLegitimo.ConfiguracionEleccion.excepcion.ExcepcionReglaNegocio;
import com.selloLegitimo.ConfiguracionEleccion.modelo.CodigoMetodoElectoral;
import com.selloLegitimo.ConfiguracionEleccion.modelo.EstadoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.ModalidadHabilitada;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoOpcionTarjeton;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoLista;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoModeloCandidatura;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoCircunscripcion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoTarjeton;
import com.selloLegitimo.ConfiguracionEleccion.servicio.IServicioEleccion;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class ConfiguracionEleccionApplicationTests {

	@Autowired
	private IServicioEleccion servicioEleccion;

	@Test
	void contextLoads() {
	}

	@Test
	void debeCrearYConsultarUnaEleccion() throws Exception {
		SolicitudCrearEleccion solicitud = new SolicitudCrearEleccion();
		solicitud.setNombreOficial("Eleccion Presidencial 2026");
		solicitud.setPais("Colombia");
		solicitud.setTipoEleccion(TipoEleccion.PRESIDENCIAL);
		solicitud.setCodigoMetodoElectoral(CodigoMetodoElectoral.ME_01);
		solicitud.setFechaInicioJornada(LocalDateTime.of(2026, 5, 29, 8, 0));
		solicitud.setFechaCierreJornada(LocalDateTime.of(2026, 5, 29, 16, 0));
		solicitud.setModalidadHabilitada(ModalidadHabilitada.AMBAS);
		solicitud.setTipoCircunscripcion(TipoCircunscripcion.TERRITORIAL);
		solicitud.setJerarquiaGeografica(List.of("Pais", "Region", "Municipio", "Puesto", "Mesa"));
		solicitud.setCircunscripcionesEspeciales(List.of("INDIGENA", "PAZ", "EXTERIOR"));
		solicitud.setZonaHoraria("America/Bogota");
		solicitud.setIdioma("es-CO");
		solicitud.setDocumentoIdentidadValido("Cedula de ciudadania");
		solicitud.setReglasElegibilidad("Ser mayor de edad y estar habilitado en el censo electoral");
		solicitud.setEstado(EstadoEleccion.BORRADOR);

		RespuestaEleccion creada = servicioEleccion.crearEleccion(solicitud);
		RespuestaEleccion consultada = servicioEleccion.obtenerEleccionPorId(creada.getId());

		assertNotNull(creada.getId());
		assertEquals("Eleccion Presidencial 2026", creada.getNombreOficial());
		assertEquals("Colombia", consultada.getPais());
		assertEquals(ModalidadHabilitada.AMBAS, consultada.getModalidadHabilitada());
		assertEquals(CodigoMetodoElectoral.ME_01, consultada.getCodigoMetodoElectoral());
		assertEquals(TipoCircunscripcion.TERRITORIAL, consultada.getTipoCircunscripcion());
		assertEquals(List.of("Pais", "Region", "Municipio", "Puesto", "Mesa"), consultada.getJerarquiaGeografica());
		assertEquals(List.of("INDIGENA", "PAZ", "EXTERIOR"), consultada.getCircunscripcionesEspeciales());
	}

	@Test
	void noDebeCrearUnaEleccionCuandoLaFechaDeCierreEsAnterior() {
		SolicitudCrearEleccion solicitud = new SolicitudCrearEleccion();
		solicitud.setNombreOficial("Eleccion Territorial 2026");
		solicitud.setPais("Colombia");
		solicitud.setTipoEleccion(TipoEleccion.TERRITORIAL);
		solicitud.setCodigoMetodoElectoral(CodigoMetodoElectoral.ME_02);
		solicitud.setFechaInicioJornada(LocalDateTime.of(2026, 10, 29, 16, 0));
		solicitud.setFechaCierreJornada(LocalDateTime.of(2026, 10, 29, 8, 0));
		solicitud.setModalidadHabilitada(ModalidadHabilitada.PRESENCIAL);
		solicitud.setTipoCircunscripcion(TipoCircunscripcion.TERRITORIAL);
		solicitud.setJerarquiaGeografica(List.of("Pais", "Departamento", "Municipio"));
		solicitud.setZonaHoraria("America/Bogota");
		solicitud.setIdioma("es-CO");
		solicitud.setDocumentoIdentidadValido("Cedula de ciudadania");
		solicitud.setReglasElegibilidad("Estar inscrito en el censo");

		ExcepcionReglaNegocio excepcion = assertThrows(ExcepcionReglaNegocio.class,
			() -> servicioEleccion.crearEleccion(solicitud));

		assertEquals("La fecha de cierre no puede ser anterior a la fecha de inicio", excepcion.getMessage());
	}

	@Test
	void noDebeCrearUnaEleccionConCircunscripcionEspecialInvalida() {
		SolicitudCrearEleccion solicitud = new SolicitudCrearEleccion();
		solicitud.setNombreOficial("Eleccion Legislativa Exterior");
		solicitud.setPais("Colombia");
		solicitud.setTipoEleccion(TipoEleccion.LEGISLATIVA);
		solicitud.setCodigoMetodoElectoral(CodigoMetodoElectoral.ME_03);
		solicitud.setFechaInicioJornada(LocalDateTime.of(2026, 3, 10, 8, 0));
		solicitud.setFechaCierreJornada(LocalDateTime.of(2026, 3, 10, 17, 0));
		solicitud.setModalidadHabilitada(ModalidadHabilitada.REMOTA);
		solicitud.setTipoCircunscripcion(TipoCircunscripcion.ESPECIAL);
		solicitud.setJerarquiaGeografica(List.of("Pais", "Consulado", "Mesa"));
		solicitud.setCircunscripcionesEspeciales(List.of("JOVENES"));
		solicitud.setZonaHoraria("America/Bogota");
		solicitud.setIdioma("es-CO");
		solicitud.setDocumentoIdentidadValido("Pasaporte");
		solicitud.setReglasElegibilidad("Ser ciudadano habilitado en el exterior");
		solicitud.setPorcentajeUmbralListas(3.0);
		solicitud.setNumeroCurules(5);

		ExcepcionReglaNegocio excepcion = assertThrows(ExcepcionReglaNegocio.class,
			() -> servicioEleccion.crearEleccion(solicitud));

		assertEquals("Las circunscripciones especiales permitidas son INDIGENA, AFRODESCENDIENTE, EXTERIOR y PAZ", excepcion.getMessage());
	}

	@Test
	void debeAdaptarModeloUninominalConViceOpcional() {
		SolicitudCrearEleccion solicitudEleccion = crearSolicitudBase(CodigoMetodoElectoral.ME_01, TipoCircunscripcion.NACIONAL);
		solicitudEleccion.setTipoEleccion(TipoEleccion.PRESIDENCIAL);
		solicitudEleccion.setJerarquiaGeografica(List.of("Pais"));
		RespuestaEleccion eleccion = servicioEleccion.crearEleccion(solicitudEleccion);

		RespuestaModeloCandidatura respuesta = servicioEleccion.obtenerModeloCandidatura(eleccion.getId());

		assertEquals(TipoModeloCandidatura.UNINOMINAL, respuesta.getTipoModeloCandidatura());
		assertEquals(true, respuesta.getPermiteFormulaVicepresidencial());
		assertEquals(true, respuesta.getFormulaVicepresidencialOpcional());
	}

	@Test
	void debeAdaptarModeloDeListaParaMe03() {
		SolicitudCrearEleccion solicitudEleccion = crearSolicitudBase(CodigoMetodoElectoral.ME_03, TipoCircunscripcion.TERRITORIAL);
		solicitudEleccion.setTipoEleccion(TipoEleccion.LEGISLATIVA);
		RespuestaEleccion eleccion = servicioEleccion.crearEleccion(solicitudEleccion);

		RespuestaModeloCandidatura respuesta = servicioEleccion.obtenerModeloCandidatura(eleccion.getId());

		assertEquals(TipoModeloCandidatura.LISTA, respuesta.getTipoModeloCandidatura());
		assertEquals(List.of(TipoLista.CERRADA, TipoLista.ABIERTA), respuesta.getTiposListaPermitidos());
		assertEquals(true, respuesta.getPermiteVotoPreferente());
	}

	@Test
	void debeAdaptarModeloDeRankingParaMe04() {
		SolicitudCrearEleccion solicitudEleccion = crearSolicitudBase(CodigoMetodoElectoral.ME_04, TipoCircunscripcion.TERRITORIAL);
		solicitudEleccion.setTipoEleccion(TipoEleccion.TERRITORIAL);
		RespuestaEleccion eleccion = servicioEleccion.crearEleccion(solicitudEleccion);

		RespuestaModeloCandidatura respuesta = servicioEleccion.obtenerModeloCandidatura(eleccion.getId());

		assertEquals(TipoModeloCandidatura.RANKING_INDIVIDUAL, respuesta.getTipoModeloCandidatura());
		assertEquals(true, respuesta.getUsaRankingPreferencias());
		assertEquals(true, respuesta.getRestringeMultiplesCircunscripcionesTerritoriales());
	}

	@Test
	void debeGenerarTarjetonConVotoEnBlancoParaMe04() {
		SolicitudCrearEleccion solicitudEleccion = crearSolicitudBase(CodigoMetodoElectoral.ME_04, TipoCircunscripcion.TERRITORIAL);
		solicitudEleccion.setTipoEleccion(TipoEleccion.TERRITORIAL);
		RespuestaEleccion eleccion = servicioEleccion.crearEleccion(solicitudEleccion);

		SolicitudGenerarTarjeton solicitud = new SolicitudGenerarTarjeton();
		solicitud.setCircunscripcion("Municipio 1");
		solicitud.setOpcionesValidadas(List.of(
			crearOpcionTarjeton("C1", TipoOpcionTarjeton.CANDIDATO, "Ana Perez", null, "Partido A", "Municipio 1", true),
			crearOpcionTarjeton("C2", TipoOpcionTarjeton.CANDIDATO, "Luis Rojas", null, "Partido B", "Municipio 1", true)
		));

		RespuestaTarjeton respuesta = servicioEleccion.generarTarjeton(eleccion.getId(), solicitud);

		assertEquals(TipoTarjeton.RANKING, respuesta.getTipoTarjeton());
		assertEquals(true, respuesta.getIncluyeVotoEnBlanco());
		assertEquals(true, respuesta.getRequiereRankingNumerado());
		assertEquals(3, respuesta.getOpciones().size());
		assertEquals(TipoOpcionTarjeton.VOTO_EN_BLANCO, respuesta.getOpciones().get(2).getTipoOpcion());
	}

	@Test
	void debeObtenerReglasDeVictoriaPorDefectoParaMe02() {
		SolicitudCrearEleccion solicitudEleccion = crearSolicitudBase(CodigoMetodoElectoral.ME_02, TipoCircunscripcion.NACIONAL);
		solicitudEleccion.setTipoEleccion(TipoEleccion.PRESIDENCIAL);
		RespuestaEleccion eleccion = servicioEleccion.crearEleccion(solicitudEleccion);

		RespuestaReglasVictoria respuesta = servicioEleccion.obtenerReglasVictoria(eleccion.getId());

		assertEquals(true, respuesta.getUsaUmbral());
		assertEquals(50.0, respuesta.getUmbralPrimeraVueltaPorcentaje());
		assertEquals(true, respuesta.getRequiereMasUnoPrimeraVuelta());
	}

	@Test
	void debeObtenerReglasConfiguradasParaMe03() {
		SolicitudCrearEleccion solicitudEleccion = crearSolicitudBase(CodigoMetodoElectoral.ME_03, TipoCircunscripcion.TERRITORIAL);
		solicitudEleccion.setPorcentajeUmbralListas(3.0);
		solicitudEleccion.setNumeroCurules(12);
		solicitudEleccion.setFormulaCifraRepartidora("D_HONDT");
		RespuestaEleccion eleccion = servicioEleccion.crearEleccion(solicitudEleccion);

		RespuestaReglasVictoria respuesta = servicioEleccion.obtenerReglasVictoria(eleccion.getId());

		assertEquals(true, respuesta.getUsaUmbral());
		assertEquals(3.0, respuesta.getPorcentajeUmbralListas());
		assertEquals(12, respuesta.getNumeroCurules());
		assertEquals("D_HONDT", respuesta.getFormulaCifraRepartidora());
	}

	private SolicitudCrearEleccion crearSolicitudBase(CodigoMetodoElectoral metodo, TipoCircunscripcion tipoCircunscripcion) {
		SolicitudCrearEleccion solicitud = new SolicitudCrearEleccion();
		solicitud.setNombreOficial("Eleccion de prueba " + metodo.name());
		solicitud.setPais("Colombia");
		solicitud.setTipoEleccion(TipoEleccion.LEGISLATIVA);
		solicitud.setCodigoMetodoElectoral(metodo);
		solicitud.setFechaInicioJornada(LocalDateTime.of(2026, 5, 29, 8, 0));
		solicitud.setFechaCierreJornada(LocalDateTime.of(2026, 5, 29, 16, 0));
		solicitud.setModalidadHabilitada(ModalidadHabilitada.AMBAS);
		solicitud.setTipoCircunscripcion(tipoCircunscripcion);
		solicitud.setJerarquiaGeografica(List.of("Pais", "Region", "Municipio"));
		solicitud.setCircunscripcionesEspeciales(List.of("INDIGENA", "PAZ"));
		solicitud.setZonaHoraria("America/Bogota");
		solicitud.setIdioma("es-CO");
		solicitud.setDocumentoIdentidadValido("Cedula de ciudadania");
		solicitud.setReglasElegibilidad("Ser ciudadano habilitado");
		if (metodo == CodigoMetodoElectoral.ME_03) {
			solicitud.setPorcentajeUmbralListas(3.0);
			solicitud.setNumeroCurules(5);
			solicitud.setFormulaCifraRepartidora("D_HONDT");
		}
		solicitud.setEstado(EstadoEleccion.BORRADOR);
		return solicitud;
	}

	private OpcionTarjetonSolicitud crearOpcionTarjeton(String codigo, TipoOpcionTarjeton tipoOpcion,
		String nombreMostrado, String nombreLista, String partidoPolitico, String circunscripcion, boolean validada) {
		OpcionTarjetonSolicitud opcion = new OpcionTarjetonSolicitud();
		opcion.setCodigo(codigo);
		opcion.setTipoOpcion(tipoOpcion);
		opcion.setNombreMostrado(nombreMostrado);
		opcion.setNombreLista(nombreLista);
		opcion.setPartidoPolitico(partidoPolitico);
		opcion.setCircunscripcion(circunscripcion);
		opcion.setValidada(validada);
		return opcion;
	}

}
