package com.selloLegitimo.ConfiguracionEleccion.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "elecciones")
public class Eleccion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nombre_oficial", nullable = false, length = 150)
	private String nombreOficial;

	@Column(name = "pais", nullable = false, length = 100)
	private String pais;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_eleccion", nullable = false, length = 50)
	private TipoEleccion tipoEleccion;

	@Column(name = "fecha_inicio_jornada", nullable = false)
	private LocalDateTime fechaInicioJornada;

	@Column(name = "fecha_cierre_jornada", nullable = false)
	private LocalDateTime fechaCierreJornada;

	@Enumerated(EnumType.STRING)
	@Column(name = "modalidad_habilitada", nullable = false, length = 20)
	private ModalidadHabilitada modalidadHabilitada;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false, length = 30)
	private EstadoEleccion estado;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombreOficial() {
		return nombreOficial;
	}

	public void setNombreOficial(String nombreOficial) {
		this.nombreOficial = nombreOficial;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public TipoEleccion getTipoEleccion() {
		return tipoEleccion;
	}

	public void setTipoEleccion(TipoEleccion tipoEleccion) {
		this.tipoEleccion = tipoEleccion;
	}

	public LocalDateTime getFechaInicioJornada() {
		return fechaInicioJornada;
	}

	public void setFechaInicioJornada(LocalDateTime fechaInicioJornada) {
		this.fechaInicioJornada = fechaInicioJornada;
	}

	public LocalDateTime getFechaCierreJornada() {
		return fechaCierreJornada;
	}

	public void setFechaCierreJornada(LocalDateTime fechaCierreJornada) {
		this.fechaCierreJornada = fechaCierreJornada;
	}

	public ModalidadHabilitada getModalidadHabilitada() {
		return modalidadHabilitada;
	}

	public void setModalidadHabilitada(ModalidadHabilitada modalidadHabilitada) {
		this.modalidadHabilitada = modalidadHabilitada;
	}

	public EstadoEleccion getEstado() {
		return estado;
	}

	public void setEstado(EstadoEleccion estado) {
		this.estado = estado;
	}
}