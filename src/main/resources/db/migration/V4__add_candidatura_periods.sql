ALTER TABLE elecciones
  ADD COLUMN fecha_inicio_mod_candidaturas TIMESTAMP,
  ADD COLUMN fecha_fin_mod_candidaturas TIMESTAMP,
  ADD COLUMN fecha_limite_reemplazo_candidaturas TIMESTAMP,
  ADD COLUMN edad_minima_candidatura INTEGER DEFAULT 18;

UPDATE elecciones
SET fecha_inicio_mod_candidaturas = '2026-01-01 00:00:00',
    fecha_fin_mod_candidaturas = '2026-12-31 23:59:59',
    fecha_limite_reemplazo_candidaturas = '2026-12-31 23:59:59',
    edad_minima_candidatura = 18
WHERE id = 1;
