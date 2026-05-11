package com.selloLegitimo.ConfiguracionEleccion.fraude.seguridad;

import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.Rol;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotacion que indica los roles permitidos sobre un endpoint del motor antifraude.
 * El InterceptorRoles valida el header X-User-Role antes de invocar el handler.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface RolPermitido {

	Rol[] value();
}
