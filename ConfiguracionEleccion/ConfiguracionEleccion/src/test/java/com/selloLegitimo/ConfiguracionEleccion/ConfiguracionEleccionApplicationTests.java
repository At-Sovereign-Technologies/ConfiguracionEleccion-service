package com.selloLegitimo.ConfiguracionEleccion;

import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaEleccion;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudCrearEleccion;
import com.selloLegitimo.ConfiguracionEleccion.excepcion.ExcepcionReglaNegocio;
import com.selloLegitimo.ConfiguracionEleccion.modelo.CodigoMetodoElectoral;
import com.selloLegitimo.ConfiguracionEleccion.modelo.EstadoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.ModalidadHabilitada;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoCircunscripcion;
import com.selloLegitimo.ConfiguracionEleccion.servicio.ServicioEleccion;
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
	private ServicioEleccion servicioEleccion;

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
		solicitud.setDocumentoNoVotable("N/A");
		solicitud.setEstado(EstadoEleccion.BORRADOR);

		RespuestaEleccion creada = servicioEleccion.crearEleccion(solicitud);
		RespuestaEleccion consultada = servicioEleccion.obtenerEleccionPorId(creada.getId());

		assertNotNull(creada.getId());
		assertEquals("Eleccion Presidencial 2026", creada.getNombreOficial());
		assertEquals("Colombia", consultada.getPais());
		assertEquals(ModalidadHabilitada.AMBAS, consultada.getModalidadHabilitada());
		assertEquals(CodigoMetodoElectoral.ME_01, consultada.getCodigoMetodoElectoral());
		assertEquals(TipoCircunscripcion.TERRITORIAL, consultada.getTipoCircunscripcion());
		assertEquals("N/A", consultada.getDocumentoNoVotable());
		assertEquals("GANA_EL_MAYOR_NUMERO_DE_VOTOS_VALIDOS", consultada.getCondicionVictoria());
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
		solicitud.setDocumentoNoVotable("N/A");

		ExcepcionReglaNegocio excepcion = assertThrows(ExcepcionReglaNegocio.class,
			() -> servicioEleccion.crearEleccion(solicitud));

		assertEquals("La fecha de cierre no puede ser anterior a la fecha de inicio", excepcion.getMessage());
	}

	@Test
	void noDebeCrearUnaEleccionMe03SinCurulesValidas() {
		SolicitudCrearEleccion solicitud = new SolicitudCrearEleccion();
		solicitud.setNombreOficial("Eleccion Legislativa Exterior");
		solicitud.setPais("Colombia");
		solicitud.setTipoEleccion(TipoEleccion.LEGISLATIVA);
		solicitud.setCodigoMetodoElectoral(CodigoMetodoElectoral.ME_03);
		solicitud.setFechaInicioJornada(LocalDateTime.of(2026, 3, 10, 8, 0));
		solicitud.setFechaCierreJornada(LocalDateTime.of(2026, 3, 10, 17, 0));
		solicitud.setModalidadHabilitada(ModalidadHabilitada.REMOTA);
		solicitud.setTipoCircunscripcion(TipoCircunscripcion.ESPECIAL);
		solicitud.setDocumentoNoVotable("TI");
		solicitud.setNumeroCurules(0);

		ExcepcionReglaNegocio excepcion = assertThrows(ExcepcionReglaNegocio.class,
			() -> servicioEleccion.crearEleccion(solicitud));

		assertEquals("ME-03 exige un numero de curules valido", excepcion.getMessage());
	}

	@Test
	void debePersistirConfiguracionMetodoMe03() {
		SolicitudCrearEleccion solicitudEleccion = crearSolicitudBase(CodigoMetodoElectoral.ME_03, TipoCircunscripcion.TERRITORIAL);
		solicitudEleccion.setTipoEleccion(TipoEleccion.LEGISLATIVA);
		solicitudEleccion.setNumeroCurules(12);
		solicitudEleccion.setFormulaCifraRepartidora("D_HONDT");
		RespuestaEleccion eleccion = servicioEleccion.crearEleccion(solicitudEleccion);

		assertEquals(12, eleccion.getNumeroCurules());
		assertEquals("D_HONDT", eleccion.getFormulaCifraRepartidora());
		assertEquals("DISTRIBUCION_PROPORCIONAL_DE_CURULES_ENTRE_LISTAS_QUE_SUPERAN_UMBRAL", eleccion.getCondicionVictoria());
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
		solicitud.setDocumentoNoVotable("N/A");
		if (metodo == CodigoMetodoElectoral.ME_03) {
			solicitud.setNumeroCurules(5);
			solicitud.setFormulaCifraRepartidora("D_HONDT");
		}
		solicitud.setEstado(EstadoEleccion.BORRADOR);
		return solicitud;
	}

}
