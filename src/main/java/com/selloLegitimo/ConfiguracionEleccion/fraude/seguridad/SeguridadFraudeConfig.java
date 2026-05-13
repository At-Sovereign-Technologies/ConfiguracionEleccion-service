package com.selloLegitimo.ConfiguracionEleccion.fraude.seguridad;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SeguridadFraudeConfig implements WebMvcConfigurer {

	@Autowired
	private InterceptorRoles interceptorRoles;

	@Autowired
	private UsuarioContextoArgumentResolver usuarioContextoArgumentResolver;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(interceptorRoles)
			.addPathPatterns("/api/v1/fraud-engine/**");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(usuarioContextoArgumentResolver);
	}
}
