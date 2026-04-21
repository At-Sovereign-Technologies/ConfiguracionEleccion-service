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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

	@Enumerated(EnumType.STRING)
	@Column(name = "codigo_metodo_electoral", nullable = false, length = 10)
	private CodigoMetodoElectoral codigoMetodoElectoral;

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

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_circunscripcion", nullable = false, length = 30)
	private TipoCircunscripcion tipoCircunscripcion;

	@Column(name = "documento_no_votable", nullable = false, length = 80)
	private String documentoNoVotable;

	@Column(name = "numero_curules")
	private Integer numeroCurules;

	@Column(name = "formula_cifra_repartidora", length = 80)
	private String formulaCifraRepartidora;

	@Column(name = "condicion_victoria", length = 180)
	private String condicionVictoria;

	// Modelos de candidatura separados por coma, ej: "ABIERTA,CERRADA"
	@Column(name = "modelos_candidatura", length = 50)
	private String modelosCandidaturaRaw;

	// Exenciones habilitadas separadas por pipe, ej: "PERSONAL ACTIVO...|DISCAPACIDAD..."
	@Column(name = "excciones_habilitadas", length = 500)
	private String excencionesHabilitadasRaw;

	// Configuracion legislativa — Senado (JSON serializado)
	@Column(name = "senado_config_json", columnDefinition = "TEXT")
	private String senadoConfigJson;

	// Configuracion legislativa — Camara: departamentos (JSON serializado)
	@Column(name = "camara_deptos_json", columnDefinition = "TEXT")
	private String camaraDeptosjson;

	// Configuracion legislativa — Camara: circunscripciones especiales (JSON serializado)
	@Column(name = "camara_especiales_json", columnDefinition = "TEXT")
	private String camaraEspecialesJson;

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

	public CodigoMetodoElectoral getCodigoMetodoElectoral() {
		return codigoMetodoElectoral;
	}

	public void setCodigoMetodoElectoral(CodigoMetodoElectoral codigoMetodoElectoral) {
		this.codigoMetodoElectoral = codigoMetodoElectoral;
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

	public TipoCircunscripcion getTipoCircunscripcion() {
		return tipoCircunscripcion;
	}

	public void setTipoCircunscripcion(TipoCircunscripcion tipoCircunscripcion) {
		this.tipoCircunscripcion = tipoCircunscripcion;
	}

	public String getDocumentoNoVotable() {
		return documentoNoVotable;
	}

	public void setDocumentoNoVotable(String documentoNoVotable) {
		this.documentoNoVotable = documentoNoVotable;
	}

	public Integer getNumeroCurules() {
		return numeroCurules;
	}

	public void setNumeroCurules(Integer numeroCurules) {
		this.numeroCurules = numeroCurules;
	}

	public String getFormulaCifraRepartidora() {
		return formulaCifraRepartidora;
	}

	public void setFormulaCifraRepartidora(String formulaCifraRepartidora) {
		this.formulaCifraRepartidora = formulaCifraRepartidora;
	}

	public String getCondicionVictoria() {
		return condicionVictoria;
	}

	public void setCondicionVictoria(String condicionVictoria) {
		this.condicionVictoria = condicionVictoria;
	}

	public List<ModeloCandidatura> getModelosCandidatura() {
		if (modelosCandidaturaRaw == null || modelosCandidaturaRaw.isBlank()) {
			return new ArrayList<>();
		}
		return Arrays.stream(modelosCandidaturaRaw.split(","))
			.map(String::trim)
			.filter(s -> !s.isBlank())
			.map(ModeloCandidatura::valueOf)
			.collect(Collectors.toList());
	}

	public void setModelosCandidatura(List<ModeloCandidatura> modelos) {
		if (modelos == null || modelos.isEmpty()) {
			this.modelosCandidaturaRaw = null;
		} else {
			this.modelosCandidaturaRaw = modelos.stream()
				.map(Enum::name)
				.collect(Collectors.joining(","));
		}
	}

	public List<String> getExcencionesHabilitadas() {
		if (excencionesHabilitadasRaw == null || excencionesHabilitadasRaw.isBlank()) {
			return new ArrayList<>();
		}
		return Arrays.stream(excencionesHabilitadasRaw.split("\\|"))
			.map(String::trim)
			.filter(s -> !s.isBlank())
			.collect(Collectors.toList());
	}

	public void setExcencionesHabilitadas(List<String> excenciones) {
		if (excenciones == null || excenciones.isEmpty()) {
			this.excencionesHabilitadasRaw = null;
		} else {
			this.excencionesHabilitadasRaw = String.join("|", excenciones);
		}
	}

	public String getSenadoConfigJson() {
		return senadoConfigJson;
	}

	public void setSenadoConfigJson(String senadoConfigJson) {
		this.senadoConfigJson = senadoConfigJson;
	}

	public String getCamaraDeptosjson() {
		return camaraDeptosjson;
	}

	public void setCamaraDeptosjson(String camaraDeptosjson) {
		this.camaraDeptosjson = camaraDeptosjson;
	}

	public String getCamaraEspecialesJson() {
		return camaraEspecialesJson;
	}

	public void setCamaraEspecialesJson(String camaraEspecialesJson) {
		this.camaraEspecialesJson = camaraEspecialesJson;
	}
}