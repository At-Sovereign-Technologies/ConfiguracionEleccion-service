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

	@Column(name = "jerarquia_geografica", nullable = false, columnDefinition = "TEXT")
	private String jerarquiaGeografica;

	@Column(name = "circunscripciones_especiales", columnDefinition = "TEXT")
	private String circunscripcionesEspeciales;

	@Column(name = "zona_horaria", nullable = false, length = 80)
	private String zonaHoraria;

	@Column(name = "idioma", nullable = false, length = 50)
	private String idioma;

	@Column(name = "documento_identidad_valido", nullable = false, length = 80)
	private String documentoIdentidadValido;

	@Column(name = "reglas_elegibilidad", nullable = false, columnDefinition = "TEXT")
	private String reglasElegibilidad;

	@Column(name = "umbral_primera_vuelta_porcentaje")
	private Double umbralPrimeraVueltaPorcentaje;

	@Column(name = "requiere_mas_uno_primera_vuelta")
	private Boolean requiereMasUnoPrimeraVuelta;

	@Column(name = "porcentaje_umbral_listas")
	private Double porcentajeUmbralListas;

	@Column(name = "numero_curules")
	private Integer numeroCurules;

	@Column(name = "formula_cifra_repartidora", length = 80)
	private String formulaCifraRepartidora;

	@Column(name = "criterio_eliminacion", length = 120)
	private String criterioEliminacion;

	@Column(name = "condicion_victoria", length = 180)
	private String condicionVictoria;

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

	public String getJerarquiaGeografica() {
		return jerarquiaGeografica;
	}

	public void setJerarquiaGeografica(String jerarquiaGeografica) {
		this.jerarquiaGeografica = jerarquiaGeografica;
	}

	public String getCircunscripcionesEspeciales() {
		return circunscripcionesEspeciales;
	}

	public void setCircunscripcionesEspeciales(String circunscripcionesEspeciales) {
		this.circunscripcionesEspeciales = circunscripcionesEspeciales;
	}

	public String getZonaHoraria() {
		return zonaHoraria;
	}

	public void setZonaHoraria(String zonaHoraria) {
		this.zonaHoraria = zonaHoraria;
	}

	public String getIdioma() {
		return idioma;
	}

	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}

	public String getDocumentoIdentidadValido() {
		return documentoIdentidadValido;
	}

	public void setDocumentoIdentidadValido(String documentoIdentidadValido) {
		this.documentoIdentidadValido = documentoIdentidadValido;
	}

	public String getReglasElegibilidad() {
		return reglasElegibilidad;
	}

	public void setReglasElegibilidad(String reglasElegibilidad) {
		this.reglasElegibilidad = reglasElegibilidad;
	}

	public Double getUmbralPrimeraVueltaPorcentaje() {
		return umbralPrimeraVueltaPorcentaje;
	}

	public void setUmbralPrimeraVueltaPorcentaje(Double umbralPrimeraVueltaPorcentaje) {
		this.umbralPrimeraVueltaPorcentaje = umbralPrimeraVueltaPorcentaje;
	}

	public Boolean getRequiereMasUnoPrimeraVuelta() {
		return requiereMasUnoPrimeraVuelta;
	}

	public void setRequiereMasUnoPrimeraVuelta(Boolean requiereMasUnoPrimeraVuelta) {
		this.requiereMasUnoPrimeraVuelta = requiereMasUnoPrimeraVuelta;
	}

	public Double getPorcentajeUmbralListas() {
		return porcentajeUmbralListas;
	}

	public void setPorcentajeUmbralListas(Double porcentajeUmbralListas) {
		this.porcentajeUmbralListas = porcentajeUmbralListas;
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

	public String getCriterioEliminacion() {
		return criterioEliminacion;
	}

	public void setCriterioEliminacion(String criterioEliminacion) {
		this.criterioEliminacion = criterioEliminacion;
	}

	public String getCondicionVictoria() {
		return condicionVictoria;
	}

	public void setCondicionVictoria(String condicionVictoria) {
		this.condicionVictoria = condicionVictoria;
	}
}