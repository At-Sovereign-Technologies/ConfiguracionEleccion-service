UPDATE elecciones
SET codigo_metodo_electoral = 'ME_01',
    estado = 'PUBLICADA',
    modelos_candidatura = 'UNICO',
    excciones_habilitadas = 'DISCAPACIDAD_REGISTRADA|FUERZA_PUBLICA_ACTIVA'
WHERE nombre_oficial = 'Eleccion Nacional 2026';