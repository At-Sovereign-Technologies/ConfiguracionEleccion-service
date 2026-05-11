package com.selloLegitimo.ConfiguracionEleccion.fraude.seguridad;

import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.Rol;

public class UsuarioContexto {

	public static final String ATRIBUTO = "usuarioContexto";

	private final String usuario;
	private final Rol rol;

	public UsuarioContexto(String usuario, Rol rol) {
		this.usuario = usuario;
		this.rol = rol;
	}

	public String getUsuario() {
		return usuario;
	}

	public Rol getRol() {
		return rol;
	}
}
