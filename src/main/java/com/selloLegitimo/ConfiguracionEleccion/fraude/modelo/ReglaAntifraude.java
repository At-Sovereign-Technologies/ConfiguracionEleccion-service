package com.selloLegitimo.ConfiguracionEleccion.fraude.modelo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "fraud_rules")
public class ReglaAntifraude {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 150, unique = true)
	private String name;

	@Column(name = "description", length = 500)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "rule_type", nullable = false, length = 40)
	private TipoRegla ruleType;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	// JSON serializado con los umbrales/ventanas/etc. configurables.
	@Column(name = "parameters", nullable = false, columnDefinition = "TEXT")
	private String parametersJson;

	@Column(name = "alert_type", nullable = false, length = 80)
	private String alertType;

	@Column(name = "risk_score_weight", nullable = false)
	private Integer riskScoreWeight;

	@Enumerated(EnumType.STRING)
	@Column(name = "approval_status", nullable = false, length = 20)
	private EstadoAprobacion approvalStatus;

	@Column(name = "created_by", nullable = false, length = 100)
	private String createdBy;

	@Column(name = "approved_by", length = 100)
	private String approvedBy;

	@Column(name = "rejection_reason", length = 500)
	private String rejectionReason;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "approved_at")
	private LocalDateTime approvedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@PrePersist
	public void prePersist() {
		LocalDateTime ahora = LocalDateTime.now();
		if (createdAt == null) {
			createdAt = ahora;
		}
		if (updatedAt == null) {
			updatedAt = ahora;
		}
		if (isActive == null) {
			isActive = Boolean.TRUE;
		}
		if (approvalStatus == null) {
			approvalStatus = EstadoAprobacion.PENDING;
		}
	}

	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Map<String, Object> getParameters() {
		if (parametersJson == null || parametersJson.isBlank()) {
			return new HashMap<>();
		}
		try {
			return MAPPER.readValue(parametersJson, new TypeReference<Map<String, Object>>() {});
		} catch (Exception e) {
			throw new IllegalStateException("Parametros JSON corruptos para la regla " + id, e);
		}
	}

	public void setParameters(Map<String, Object> parameters) {
		if (parameters == null || parameters.isEmpty()) {
			this.parametersJson = "{}";
			return;
		}
		try {
			this.parametersJson = MAPPER.writeValueAsString(parameters);
		} catch (Exception e) {
			throw new IllegalArgumentException("No se pudieron serializar los parametros", e);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TipoRegla getRuleType() {
		return ruleType;
	}

	public void setRuleType(TipoRegla ruleType) {
		this.ruleType = ruleType;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getParametersJson() {
		return parametersJson;
	}

	public void setParametersJson(String parametersJson) {
		this.parametersJson = parametersJson;
	}

	public String getAlertType() {
		return alertType;
	}

	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	public Integer getRiskScoreWeight() {
		return riskScoreWeight;
	}

	public void setRiskScoreWeight(Integer riskScoreWeight) {
		this.riskScoreWeight = riskScoreWeight;
	}

	public EstadoAprobacion getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(EstadoAprobacion approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public LocalDateTime getApprovedAt() {
		return approvedAt;
	}

	public void setApprovedAt(LocalDateTime approvedAt) {
		this.approvedAt = approvedAt;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}
}
