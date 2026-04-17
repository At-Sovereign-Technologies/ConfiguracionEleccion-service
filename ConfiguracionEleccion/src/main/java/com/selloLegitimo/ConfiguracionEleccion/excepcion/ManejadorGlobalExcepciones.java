package com.selloLegitimo.ConfiguracionEleccion.excepcion;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

	private static final Logger logger = LoggerFactory.getLogger(ManejadorGlobalExcepciones.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException excepcion) {
		Map<String, String> errores = new LinkedHashMap<>();
		for (FieldError error : excepcion.getBindingResult().getFieldErrors()) {
			errores.put(error.getField(), error.getDefaultMessage());
		}

		logger.warn("Solicitud invalida recibida: {}", errores);
		return ResponseEntity.badRequest().body(crearRespuesta(HttpStatus.BAD_REQUEST, "Error de validacion", errores));
	}

	@ExceptionHandler(ExcepcionReglaNegocio.class)
	public ResponseEntity<Map<String, Object>> manejarReglas(ExcepcionReglaNegocio excepcion) {
		logger.warn("Regla de negocio incumplida: {}", excepcion.getMessage());
		return ResponseEntity.badRequest().body(crearRespuesta(HttpStatus.BAD_REQUEST, excepcion.getMessage(), null));
	}

	@ExceptionHandler(ExcepcionRecursoNoEncontrado.class)
	public ResponseEntity<Map<String, Object>> manejarNoEncontrado(ExcepcionRecursoNoEncontrado excepcion) {
		logger.warn("Recurso no encontrado: {}", excepcion.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(crearRespuesta(HttpStatus.NOT_FOUND, excepcion.getMessage(), null));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> manejarGeneral(Exception excepcion) {
		logger.error("Error no controlado", excepcion);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(crearRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servicio", null));
	}

	private Map<String, Object> crearRespuesta(HttpStatus estado, String mensaje, Object errores) {
		Map<String, Object> respuesta = new LinkedHashMap<>();
		respuesta.put("fecha", LocalDateTime.now());
		respuesta.put("codigo", estado.value());
		respuesta.put("mensaje", mensaje);
		if (errores != null) {
			respuesta.put("errores", errores);
		}
		return respuesta;
	}
}