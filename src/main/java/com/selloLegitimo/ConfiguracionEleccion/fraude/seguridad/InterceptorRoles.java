package com.selloLegitimo.ConfiguracionEleccion.fraude.seguridad;

import com.selloLegitimo.ConfiguracionEleccion.fraude.excepcion.ExcepcionAccesoDenegado;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.Rol;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Resuelve el usuario y el rol a partir de los headers X-User-Id y X-User-Role
 * y valida que el rol coincida con la anotacion @RolPermitido del handler.
 *
 * En produccion estos datos vendrian del token JWT emitido por el modulo de
 * autenticacion biometrica; aqui los recibimos por header para mantener al
 * motor antifraude desacoplado de la capa de identidad.
 */
@Component
public class InterceptorRoles implements HandlerInterceptor {

	public static final String HEADER_USUARIO = "X-User-Id";
	public static final String HEADER_ROL = "X-User-Role";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (!(handler instanceof HandlerMethod handlerMethod)) {
			return true;
		}

		RolPermitido anotacion = handlerMethod.getMethodAnnotation(RolPermitido.class);
		if (anotacion == null) {
			anotacion = handlerMethod.getBeanType().getAnnotation(RolPermitido.class);
		}
		if (anotacion == null) {
			return true;
		}

		String usuarioId = request.getHeader(HEADER_USUARIO);
		String rolHeader = request.getHeader(HEADER_ROL);

		if (usuarioId == null || usuarioId.isBlank()) {
			throw new ExcepcionAccesoDenegado("Header " + HEADER_USUARIO + " ausente");
		}
		if (rolHeader == null || rolHeader.isBlank()) {
			throw new ExcepcionAccesoDenegado("Header " + HEADER_ROL + " ausente");
		}

		Rol rolUsuario;
		try {
			rolUsuario = Rol.valueOf(rolHeader.trim());
		} catch (IllegalArgumentException e) {
			throw new ExcepcionAccesoDenegado("Rol '" + rolHeader + "' no reconocido");
		}

		boolean permitido = Arrays.asList(anotacion.value()).contains(rolUsuario);
		if (!permitido) {
			throw new ExcepcionAccesoDenegado(
				"El rol " + rolUsuario + " no tiene permisos para este endpoint");
		}

		request.setAttribute(UsuarioContexto.ATRIBUTO, new UsuarioContexto(usuarioId.trim(), rolUsuario));
		return true;
	}
}
