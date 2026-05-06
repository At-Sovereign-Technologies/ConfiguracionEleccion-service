-- ============================================================
-- V5: Seed de elecciones adicionales para pruebas integrales
-- ============================================================
-- Este migration añade elecciones legislativas y territoriales
-- que comparten el mismo universo de ciudadanos/candidaturas.
-- Las elecciones mantienen coherencia con las excenciones y
-- periodos de candidaturas configurados en V4.
-- ============================================================

-- Elección Legislativa 2026 (id=2)
-- Congreso/Senado - circunscripción NACIONAL y ESPECIAL
INSERT INTO elecciones (
    nombre_oficial,
    pais,
    tipo_eleccion,
    codigo_metodo_electoral,
    fecha_inicio_jornada,
    fecha_cierre_jornada,
    modalidad_habilitada,
    tipo_circunscripcion,
    documento_no_votable,
    numero_curules,
    formula_cifra_repartidora,
    condicion_victoria,
    estado,
    modelos_candidatura,
    excciones_habilitadas,
    senado_config_json,
    camara_deptos_json,
    camara_especiales_json,
    fecha_inicio_mod_candidaturas,
    fecha_fin_mod_candidaturas,
    fecha_limite_reemplazo_candidaturas,
    edad_minima_candidatura
) VALUES (
    'Eleccion Legislativa 2026',
    'Colombia',
    'LEGISLATIVA',
    'ME_01',
    '2026-05-10 08:00:00',
    '2026-05-10 16:00:00',
    'PRESENCIAL',
    'NACIONAL',
    'TI',
    108,
    'Divisor D''Hondt',
    'Umbral del 3%',
    'PUBLICADA',
    'ABIERTA',
    'DISCAPACIDAD_REGISTRADA|FUERZA_PUBLICA_ACTIVA',
    '{"senators_per_dept": 2, "national_constituency": 100}',
    NULL,
    NULL,
    '2026-01-01 00:00:00',
    '2026-12-31 23:59:59',
    '2026-12-31 23:59:59',
    18
);

-- Elección Territorial - Gobernador de Antioquia 2027 (id=3)
-- Circunscripción TERRITORIAL (departamental)
INSERT INTO elecciones (
    nombre_oficial,
    pais,
    tipo_eleccion,
    codigo_metodo_electoral,
    fecha_inicio_jornada,
    fecha_cierre_jornada,
    modalidad_habilitada,
    tipo_circunscripcion,
    documento_no_votable,
    numero_curules,
    formula_cifra_repartidora,
    condicion_victoria,
    estado,
    modelos_candidatura,
    excciones_habilitadas,
    senado_config_json,
    camara_deptos_json,
    camara_especiales_json,
    fecha_inicio_mod_candidaturas,
    fecha_fin_mod_candidaturas,
    fecha_limite_reemplazo_candidaturas,
    edad_minima_candidatura
) VALUES (
    'Eleccion Gobernador Antioquia 2027',
    'Colombia',
    'TERRITORIAL',
    'ME_01',
    '2027-03-15 08:00:00',
    '2027-03-15 16:00:00',
    'PRESENCIAL',
    'TERRITORIAL',
    'TI',
    1,
    NULL,
    'Mayoria simple',
    'BORRADOR',
    'UNICO',
    'DISCAPACIDAD_REGISTRADA|FUERZA_PUBLICA_ACTIVA',
    NULL,
    '{"department": "Antioquia", "seats": 1}',
    NULL,
    '2027-01-01 00:00:00',
    '2027-02-28 23:59:59',
    '2027-03-01 23:59:59',
    18
);

-- Elección Territorial - Alcalde de Bogotá 2027 (id=4)
-- Circunscripción ESPECIAL (capital district)
INSERT INTO elecciones (
    nombre_oficial,
    pais,
    tipo_eleccion,
    codigo_metodo_electoral,
    fecha_inicio_jornada,
    fecha_cierre_jornada,
    modalidad_habilitada,
    tipo_circunscripcion,
    documento_no_votable,
    numero_curules,
    formula_cifra_repartidora,
    condicion_victoria,
    estado,
    modelos_candidatura,
    excciones_habilitadas,
    senado_config_json,
    camara_deptos_json,
    camara_especiales_json,
    fecha_inicio_mod_candidaturas,
    fecha_fin_mod_candidaturas,
    fecha_limite_reemplazo_candidaturas,
    edad_minima_candidatura
) VALUES (
    'Eleccion Alcalde Bogota 2027',
    'Colombia',
    'TERRITORIAL',
    'ME_01',
    '2027-03-15 08:00:00',
    '2027-03-15 16:00:00',
    'PRESENCIAL',
    'ESPECIAL',
    'TI',
    1,
    NULL,
    'Mayoria simple',
    'PUBLICADA',
    'UNICO',
    'FUERZA_PUBLICA_ACTIVA',
    NULL,
    NULL,
    '{"special_district": "Bogota D.C.", "seats": 1}',
    '2027-01-01 00:00:00',
    '2027-02-28 23:59:59',
    '2027-03-01 23:59:59',
    25
);
