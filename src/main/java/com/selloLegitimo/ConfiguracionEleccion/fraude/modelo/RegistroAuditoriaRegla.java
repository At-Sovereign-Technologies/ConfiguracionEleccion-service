package com.selloLegitimo.ConfiguracionEleccion.fraude.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_rules_audit")
public class RegistroAuditoriaRegla {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "rule_id", nullable = false)
	private Long ruleId;

	@Enumerated(EnumType.STRING)
	@Column(name = "accion", nullable = false, length = 30)
	private AccionAuditoria accion;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado_anterior", length = 20)
	private EstadoAprobacion estadoAnterior;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado_nuevo", length = 20)
	private EstadoAprobacion estadoNuevo;

	@Column(name = "usuario", nullable = false, length = 100)
	private String usuario;

	@Enumerated(EnumType.STRING)
	@Column(name = "rol_usuario", nullable = false, length = 30)
	private Rol rolUsuario;

	@Column(name = "motivo", length = 500)
	private String motivo;

	@Column(name = "snapshot", columnDefinition = "TEXT")
	private String snapshot;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void prePersist() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public AccionAuditoria getAccion() {
		return accion;
	}

	public void setAccion(AccionAuditoria accion) {
		this.accion = accion;
	}

	public EstadoAprobacion getEstadoAnterior() {
		return estadoAnterior;
	}

	public void setEstadoAnterior(EstadoAprobacion estadoAnterior) {
		this.estadoAnterior = estadoAnterior;
	}

	public EstadoAprobacion getEstadoNuevo() {
		return estadoNuevo;
	}

	public void setEstadoNuevo(EstadoAprobacion estadoNuevo) {
		this.estadoNuevo = estadoNuevo;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Rol getRolUsuario() {
		return rolUsuario;
	}

	public void setRolUsuario(Rol rolUsuario) {
		this.rolUsuario = rolUsuario;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(String snapshot) {
		this.snapshot = snapshot;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
