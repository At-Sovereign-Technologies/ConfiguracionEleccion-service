-- Motor antifraude configurable
-- Tablas para almacenar reglas y su auditoria

CREATE TABLE fraud_rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    description VARCHAR(500),
    rule_type VARCHAR(40) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    parameters TEXT NOT NULL,
    alert_type VARCHAR(80) NOT NULL,
    risk_score_weight INTEGER NOT NULL,
    approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_by VARCHAR(100) NOT NULL,
    approved_by VARCHAR(100),
    rejection_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    approved_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT chk_fraud_rules_score_weight CHECK (risk_score_weight BETWEEN 0 AND 100),
    CONSTRAINT chk_fraud_rules_approval_status CHECK (approval_status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

CREATE INDEX idx_fraud_rules_active_approved ON fraud_rules (is_active, approval_status);
CREATE INDEX idx_fraud_rules_rule_type ON fraud_rules (rule_type);

CREATE TABLE fraud_rules_audit (
    id BIGSERIAL PRIMARY KEY,
    rule_id BIGINT NOT NULL,
    accion VARCHAR(30) NOT NULL,
    estado_anterior VARCHAR(20),
    estado_nuevo VARCHAR(20),
    usuario VARCHAR(100) NOT NULL,
    rol_usuario VARCHAR(30) NOT NULL,
    motivo VARCHAR(500),
    snapshot TEXT,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_fraud_rules_audit_rule FOREIGN KEY (rule_id) REFERENCES fraud_rules (id)
);

CREATE INDEX idx_fraud_rules_audit_rule ON fraud_rules_audit (rule_id);
CREATE INDEX idx_fraud_rules_audit_created_at ON fraud_rules_audit (created_at);
