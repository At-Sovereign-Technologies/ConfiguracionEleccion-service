package com.selloLegitimo.ConfiguracionEleccion.controlador;

import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaEleccion;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudCrearEleccion;
import com.selloLegitimo.ConfiguracionEleccion.servicio.ServicioEleccion;
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
	private ServicioEleccion servicioEleccion;

	@GetMapping
	public ResponseEntity<java.util.List<RespuestaEleccion>> listarElecciones() {
		logger.info("Solicitud recibida para listar todas las elecciones");
		return ResponseEntity.ok(servicioEleccion.listarElecciones());
	}

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

}