package com.selloLegitimo.ConfiguracionEleccion.controlador;

import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaEleccion;
import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaModeloCandidatura;
import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaReglasVictoria;
import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaTarjeton;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudGenerarTarjeton;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudCrearEleccion;
import com.selloLegitimo.ConfiguracionEleccion.servicio.IServicioEleccion;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/elecciones")
public class ControladorEleccion {

	private static final Logger logger = LoggerFactory.getLogger(ControladorEleccion.class);

	@Autowired
	private IServicioEleccion servicioEleccion;

	@PostMapping
	public ResponseEntity<RespuestaEleccion> crearEleccion(@Valid @RequestBody SolicitudCrearEleccion solicitud) {
		logger.info("Solicitud recibida para crear una eleccion con nombre {}", solicitud.getNombreOficial());
		RespuestaEleccion respuesta = servicioEleccion.crearEleccion(solicitud);
		return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}

	@GetMapping("/{id}")
	public ResponseEntity<RespuestaEleccion> obtenerEleccion(@PathVariable Long id) {
		logger.info("Solicitud recibida para consultar la eleccion {}", id);
		return ResponseEntity.ok(servicioEleccion.obtenerEleccionPorId(id));
	}

	@GetMapping("/{idEleccion}/modelo-candidatura")
	public ResponseEntity<RespuestaModeloCandidatura> obtenerModeloCandidatura(@PathVariable Long idEleccion) {
		logger.info("Solicitud recibida para consultar el modelo de candidatura de la eleccion {}", idEleccion);
		return ResponseEntity.ok(servicioEleccion.obtenerModeloCandidatura(idEleccion));
	}

	@PostMapping("/{idEleccion}/tarjeton")
	public ResponseEntity<RespuestaTarjeton> generarTarjeton(@PathVariable Long idEleccion,
		@Valid @RequestBody SolicitudGenerarTarjeton solicitud) {
		logger.info("Solicitud recibida para generar el tarjeton de la eleccion {}", idEleccion);
		return ResponseEntity.ok(servicioEleccion.generarTarjeton(idEleccion, solicitud));
	}

	@GetMapping("/{idEleccion}/reglas-victoria")
	public ResponseEntity<RespuestaReglasVictoria> obtenerReglasVictoria(@PathVariable Long idEleccion) {
		logger.info("Solicitud recibida para consultar las reglas de victoria de la eleccion {}", idEleccion);
		return ResponseEntity.ok(servicioEleccion.obtenerReglasVictoria(idEleccion));
	}
}