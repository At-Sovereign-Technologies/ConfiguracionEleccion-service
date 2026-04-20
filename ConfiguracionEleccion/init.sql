CREATE TABLE IF NOT EXISTS elecciones (
    id BIGSERIAL PRIMARY KEY,
    nombre_oficial VARCHAR(150) NOT NULL,
    pais VARCHAR(100) NOT NULL,
    tipo_eleccion VARCHAR(50) NOT NULL,
    codigo_metodo_electoral VARCHAR(10) NOT NULL,
    fecha_inicio_jornada TIMESTAMP NOT NULL,
    fecha_cierre_jornada TIMESTAMP NOT NULL,
    modalidad_habilitada VARCHAR(20) NOT NULL,
    tipo_circunscripcion VARCHAR(30) NOT NULL,
    documento_no_votable VARCHAR(80) NOT NULL,
    numero_curules INTEGER,
    formula_cifra_repartidora VARCHAR(80),
    condicion_victoria VARCHAR(180),
    estado VARCHAR(30) NOT NULL
);

ALTER TABLE elecciones
	ADD COLUMN IF NOT EXISTS documento_no_votable VARCHAR(80);

ALTER TABLE elecciones
    ADD COLUMN IF NOT EXISTS numero_curules INTEGER;

ALTER TABLE elecciones
    ADD COLUMN IF NOT EXISTS formula_cifra_repartidora VARCHAR(80);

ALTER TABLE elecciones
    ADD COLUMN IF NOT EXISTS condicion_victoria VARCHAR(180);

UPDATE elecciones
SET documento_no_votable = COALESCE(documento_no_votable, 'N/A')
WHERE documento_no_votable IS NULL;

ALTER TABLE elecciones
	ALTER COLUMN documento_no_votable SET NOT NULL;

ALTER TABLE elecciones DROP COLUMN IF EXISTS jerarquia_geografica;
ALTER TABLE elecciones DROP COLUMN IF EXISTS circunscripciones_especiales;
ALTER TABLE elecciones DROP COLUMN IF EXISTS zona_horaria;
ALTER TABLE elecciones DROP COLUMN IF EXISTS idioma;
ALTER TABLE elecciones DROP COLUMN IF EXISTS reglas_elegibilidad;
ALTER TABLE elecciones DROP COLUMN IF EXISTS umbral_primera_vuelta_porcentaje;
ALTER TABLE elecciones DROP COLUMN IF EXISTS requiere_mas_uno_primera_vuelta;
ALTER TABLE elecciones DROP COLUMN IF EXISTS porcentaje_umbral_listas;
ALTER TABLE elecciones DROP COLUMN IF EXISTS criterio_eliminacion;