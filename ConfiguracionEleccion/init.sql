CREATE TABLE IF NOT EXISTS elecciones (
    id BIGSERIAL PRIMARY KEY,
    nombre_oficial VARCHAR(150) NOT NULL,
    pais VARCHAR(100) NOT NULL,
    tipo_eleccion VARCHAR(50) NOT NULL,
    fecha_inicio_jornada TIMESTAMP NOT NULL,
    fecha_cierre_jornada TIMESTAMP NOT NULL,
    modalidad_habilitada VARCHAR(20) NOT NULL,
    estado VARCHAR(30) NOT NULL
);