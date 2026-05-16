package com.selloLegitimo.ConfiguracionEleccion.fraude.controlador;

import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.RespuestaReglaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.SolicitudCrearReglaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.SolicitudEditarReglaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.SolicitudRechazoDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.Rol;
import com.selloLegitimo.ConfiguracionEleccion.fraude.seguridad.RolPermitido;
import com.selloLegitimo.ConfiguracionEleccion.fraude.seguridad.UsuarioContexto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.servicio.IServicioReglasAntifraude;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraud-engine/rules")
public class ControladorReglasAntifraude {

	private static final Logger logger = LoggerFactory.getLogger(ControladorReglasAntifraude.class);

	@Autowired
	private IServicioReglasAntifraude servicioReglas;

	@GetMapping
	@RolPermitido({ Rol.ADMIN_RNEC, Rol.DELEGADO_CNE, Rol.ADMINISTRADOR, Rol.SUPERADMIN, Rol.AUDITOR, Rol.OPERADOR, Rol.MAGISTRADO, Rol.REGISTRADOR })
	public ResponseEntity<List<RespuestaReglaDto>> listar() {
		logger.info("Solicitud para listar reglas antifraude");
		return ResponseEntity.ok(servicioReglas.listar());
	}

	@GetMapping("/{id}")
	@RolPermitido({ Rol.ADMIN_RNEC, Rol.DELEGADO_CNE, Rol.ADMINISTRADOR, Rol.SUPERADMIN, Rol.AUDITOR, Rol.OPERADOR, Rol.MAGISTRADO, Rol.REGISTRADOR })
	public ResponseEntity<RespuestaReglaDto> obtener(@PathVariable Long id) {
		return ResponseEntity.ok(servicioReglas.obtenerPorId(id));
	}

	@PostMapping
	@RolPermitido({ Rol.ADMIN_RNEC, Rol.ADMINISTRADOR, Rol.SUPERADMIN })
	public ResponseEntity<RespuestaReglaDto> crear(@Valid @RequestBody SolicitudCrearReglaDto solicitud,
		UsuarioContexto usuario) {
		logger.info("Creacion de regla {} por {}", solicitud.getName(), usuario.getUsuario());
		RespuestaReglaDto respuesta = servicioReglas.crear(solicitud, usuario);
		return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}

	@PatchMapping("/{id}")
	@RolPermitido({ Rol.ADMIN_RNEC, Rol.ADMINISTRADOR, Rol.SUPERADMIN })
	public ResponseEntity<RespuestaReglaDto> editar(@PathVariable Long id,
		@Valid @RequestBody SolicitudEditarReglaDto solicitud, UsuarioContexto usuario) {
		logger.info("Edicion de regla {} por {}", id, usuario.getUsuario());
		return ResponseEntity.ok(servicioReglas.editar(id, solicitud, usuario));
	}

	@PostMapping("/{id}/approve")
	@RolPermitido({ Rol.DELEGADO_CNE, Rol.SUPERADMIN })
	public ResponseEntity<RespuestaReglaDto> aprobar(@PathVariable Long id, UsuarioContexto usuario) {
		logger.info("Aprobacion de regla {} por delegado {}", id, usuario.getUsuario());
		return ResponseEntity.ok(servicioReglas.aprobar(id, usuario));
	}

	@PostMapping("/{id}/reject")
	@RolPermitido({ Rol.DELEGADO_CNE, Rol.SUPERADMIN })
	public ResponseEntity<RespuestaReglaDto> rechazar(@PathVariable Long id,
		@Valid @RequestBody SolicitudRechazoDto solicitud, UsuarioContexto usuario) {
		logger.info("Rechazo de regla {} por delegado {}", id, usuario.getUsuario());
		return ResponseEntity.ok(servicioReglas.rechazar(id, solicitud.getMotivo(), usuario));
	}

	@DeleteMapping("/{id}")
	@RolPermitido({ Rol.DELEGADO_CNE, Rol.SUPERADMIN })
	public ResponseEntity<RespuestaReglaDto> desactivar(@PathVariable Long id, UsuarioContexto usuario) {
		logger.info("Desactivacion de regla {} por delegado {}", id, usuario.getUsuario());
		return ResponseEntity.ok(servicioReglas.desactivar(id, usuario));
	}
}
