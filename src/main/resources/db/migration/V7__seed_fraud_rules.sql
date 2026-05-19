INSERT INTO fraud_rules (name, description, rule_type, is_active, parameters, alert_type, risk_score_weight, approval_status, created_by, approved_by, created_at, updated_at, approved_at)
VALUES
(
    'Autenticaciones fallidas reiteradas',
    'Detecta multiples intentos fallidos de autenticacion para un mismo documento en una ventana de tiempo corta.',
    'FAILED_AUTH_ATTEMPTS', true,
    '{"maxFailedAttempts": 5, "windowMinutes": 15}',
    'AUTH_FAILURES',
    30, 'APPROVED',
    'admin.rnec.01', 'delegado.cne.01',
    NOW(), NOW(), NOW()
),
(
    'Inconsistencia biometrica',
    'Genera alerta cuando se supera el umbral de fallos de coincidencia biometrica para un mismo documento en un periodo de horas.',
    'BIOMETRIC_INCONSISTENCY', true,
    '{"maxInconsistencies": 3, "windowHours": 24}',
    'BIOMETRIC_MISMATCH',
    40, 'APPROVED',
    'admin.rnec.01', 'delegado.cne.01',
    NOW(), NOW(), NOW()
),
(
    'Intento de voto duplicado',
    'Detecta cuando un mismo numero de documento intenta votar mas de una vez en una ventana de tiempo.',
    'DUPLICATE_VOTE_ATTEMPT', true,
    '{"windowHours": 1}',
    'DUPLICATE_VOTE',
    50, 'APPROVED',
    'admin.rnec.01', 'delegado.cne.01',
    NOW(), NOW(), NOW()
),
(
    'Patron temporal anomalo',
    'Identifica un numero excesivo de autenticaciones desde una misma mesa en un intervalo muy corto de segundos.',
    'ANOMALOUS_TIME_PATTERN', true,
    '{"maxAuthInWindow": 5, "windowSeconds": 60}',
    'ANOMALOUS_PATTERN',
    25, 'APPROVED',
    'admin.rnec.01', 'delegado.cne.01',
    NOW(), NOW(), NOW()
),
(
    'Comportamiento irregular entre mesas',
    'Compara el volumen de votacion de una mesa contra el promedio de las mesas del mismo puesto. Dispara alerta si la desviacion supera el umbral configurado.',
    'IRREGULAR_TABLE_BEHAVIOR', true,
    '{"deviationThreshold": 30, "minComparableTables": 5}',
    'TABLE_ANOMALY',
    35, 'APPROVED',
    'admin.rnec.01', 'delegado.cne.01',
    NOW(), NOW(), NOW()
);
