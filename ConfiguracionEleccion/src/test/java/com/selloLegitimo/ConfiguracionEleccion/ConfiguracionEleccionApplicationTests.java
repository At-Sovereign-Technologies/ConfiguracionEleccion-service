package com.selloLegitimo.ConfiguracionEleccion;

import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaEleccion;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudCrearEleccion;
import com.selloLegitimo.ConfiguracionEleccion.excepcion.ExcepcionReglaNegocio;
import com.selloLegitimo.ConfiguracionEleccion.modelo.EstadoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.ModalidadHabilitada;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.servicio.IServicioEleccion;
import java.time.LocalDateTime;
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
		solicitud.setFechaInicioJornada(LocalDateTime.of(2026, 5, 29, 8, 0));
		solicitud.setFechaCierreJornada(LocalDateTime.of(2026, 5, 29, 16, 0));
		solicitud.setModalidadHabilitada(ModalidadHabilitada.AMBAS);
		solicitud.setEstado(EstadoEleccion.BORRADOR);

		RespuestaEleccion creada = servicioEleccion.crearEleccion(solicitud);
		RespuestaEleccion consultada = servicioEleccion.obtenerEleccionPorId(creada.getId());

		assertNotNull(creada.getId());
		assertEquals("Eleccion Presidencial 2026", creada.getNombreOficial());
		assertEquals("Colombia", consultada.getPais());
		assertEquals(ModalidadHabilitada.AMBAS, consultada.getModalidadHabilitada());
	}

	@Test
	void noDebeCrearUnaEleccionCuandoLaFechaDeCierreEsAnterior() {
		SolicitudCrearEleccion solicitud = new SolicitudCrearEleccion();
		solicitud.setNombreOficial("Eleccion Territorial 2026");
		solicitud.setPais("Colombia");
		solicitud.setTipoEleccion(TipoEleccion.TERRITORIAL);
		solicitud.setFechaInicioJornada(LocalDateTime.of(2026, 10, 29, 16, 0));
		solicitud.setFechaCierreJornada(LocalDateTime.of(2026, 10, 29, 8, 0));
		solicitud.setModalidadHabilitada(ModalidadHabilitada.PRESENCIAL);

		ExcepcionReglaNegocio excepcion = assertThrows(ExcepcionReglaNegocio.class,
			() -> servicioEleccion.crearEleccion(solicitud));

		assertEquals("La fecha de cierre no puede ser anterior a la fecha de inicio", excepcion.getMessage());
	}

}
