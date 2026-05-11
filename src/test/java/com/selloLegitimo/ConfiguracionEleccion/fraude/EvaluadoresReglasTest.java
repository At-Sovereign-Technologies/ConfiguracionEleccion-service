package com.selloLegitimo.ConfiguracionEleccion.fraude;

import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.AlertaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.EventoVotacionDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.VotingEventContext;
import com.selloLegitimo.ConfiguracionEleccion.fraude.evaluador.EvaluadorAnomalousTimePattern;
import com.selloLegitimo.ConfiguracionEleccion.fraude.evaluador.EvaluadorBiometricInconsistency;
import com.selloLegitimo.ConfiguracionEleccion.fraude.evaluador.EvaluadorDuplicateVoteAttempt;
import com.selloLegitimo.ConfiguracionEleccion.fraude.evaluador.EvaluadorFailedAuthAttempts;
import com.selloLegitimo.ConfiguracionEleccion.fraude.evaluador.EvaluadorIrregularTableBehavior;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.EstadoAprobacion;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.ReglaAntifraude;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.Severidad;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.TipoEventoVotacion;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.TipoRegla;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvaluadoresReglasTest {

	private static final LocalDateTime AHORA = LocalDateTime.of(2026, 5, 11, 10, 30);

	@Test
	void failedAuthAttempts_disparaAlertaCuandoSeAlcanzaElUmbralEnLaMesa() {
		ReglaAntifraude regla = construirRegla(TipoRegla.FAILED_AUTH_ATTEMPTS, 40,
			Map.of("maxFailedAttempts", 3, "windowMinutes", 10),
			"FAILED_AUTH_ATTEMPTS_ALERT");

		List<EventoVotacionDto> historicos = new ArrayList<>();
		historicos.add(autenticacion("MESA-1", "DOC-1", false, AHORA.minusMinutes(8)));
		historicos.add(autenticacion("MESA-1", "DOC-1", false, AHORA.minusMinutes(5)));
		historicos.add(autenticacion("MESA-1", "DOC-1", true, AHORA.minusMinutes(15))); // fuera de ventana
		EventoVotacionDto actual = autenticacion("MESA-1", "DOC-1", false, AHORA);

		Optional<AlertaDto> alerta = new EvaluadorFailedAuthAttempts().evaluar(regla,
			VotingEventContext.builder().eventoActual(actual).eventosHistoricos(historicos).build());

		assertTrue(alerta.isPresent(), "Se esperaba alerta por intentos fallidos");
		assertEquals("FAILED_AUTH_ATTEMPTS_ALERT", alerta.get().getAlertType());
		assertEquals("MESA-1", alerta.get().getTableId());
		assertEquals(3L, alerta.get().getDetails().get("intentosFallidos"));
		assertEquals(Severidad.MEDIUM, alerta.get().getSeverity());
	}

	@Test
	void failedAuthAttempts_noDisparaPorDebajoDelUmbral() {
		ReglaAntifraude regla = construirRegla(TipoRegla.FAILED_AUTH_ATTEMPTS, 40,
			Map.of("maxFailedAttempts", 5, "windowMinutes", 10), "X");

		List<EventoVotacionDto> historicos = List.of(
			autenticacion("MESA-1", "DOC-1", false, AHORA.minusMinutes(5)));
		EventoVotacionDto actual = autenticacion("MESA-1", "DOC-1", false, AHORA);

		Optional<AlertaDto> alerta = new EvaluadorFailedAuthAttempts().evaluar(regla,
			VotingEventContext.builder().eventoActual(actual).eventosHistoricos(historicos).build());
		assertFalse(alerta.isPresent());
	}

	@Test
	void biometricInconsistency_disparaConVariosFallosBiometricos() {
		ReglaAntifraude regla = construirRegla(TipoRegla.BIOMETRIC_INCONSISTENCY, 70,
			Map.of("maxInconsistencies", 2, "windowHours", 4), "BIO_INCONSISTENCY");

		List<EventoVotacionDto> historicos = new ArrayList<>();
		historicos.add(autenticacionBiometrica("MESA-7", "DOC-99", false, AHORA.minusHours(1)));
		historicos.add(autenticacionBiometrica("MESA-3", "DOC-99", false, AHORA.minusHours(2)));
		EventoVotacionDto actual = autenticacionBiometrica("MESA-7", "DOC-99", false, AHORA);

		Optional<AlertaDto> alerta = new EvaluadorBiometricInconsistency().evaluar(regla,
			VotingEventContext.builder().eventoActual(actual).eventosHistoricos(historicos).build());

		assertTrue(alerta.isPresent());
		assertEquals("DOC-99", alerta.get().getDocumentId());
		assertEquals(3L, alerta.get().getDetails().get("inconsistencias"));
		assertEquals(Severidad.HIGH, alerta.get().getSeverity());
	}

	@Test
	void duplicateVoteAttempt_disparaCuandoYaHayVotoEnLaVentana() {
		ReglaAntifraude regla = construirRegla(TipoRegla.DUPLICATE_VOTE_ATTEMPT, 95,
			Map.of("windowHours", 24), "DUPLICATE_VOTE");

		List<EventoVotacionDto> historicos = List.of(
			voto("MESA-2", "DOC-7", true, AHORA.minusHours(3)));
		EventoVotacionDto actual = voto("MESA-2", "DOC-7", true, AHORA);

		Optional<AlertaDto> alerta = new EvaluadorDuplicateVoteAttempt().evaluar(regla,
			VotingEventContext.builder().eventoActual(actual).eventosHistoricos(historicos).build());

		assertTrue(alerta.isPresent());
		assertEquals(Severidad.CRITICAL, alerta.get().getSeverity());
		assertEquals(1L, alerta.get().getDetails().get("votosPreviosRegistrados"));
	}

	@Test
	void duplicateVoteAttempt_noDisparaParaEventosDeAutenticacion() {
		ReglaAntifraude regla = construirRegla(TipoRegla.DUPLICATE_VOTE_ATTEMPT, 95,
			Map.of("windowHours", 24), "DUPLICATE_VOTE");
		EventoVotacionDto actual = autenticacion("MESA-2", "DOC-7", true, AHORA);

		Optional<AlertaDto> alerta = new EvaluadorDuplicateVoteAttempt().evaluar(regla,
			VotingEventContext.builder().eventoActual(actual).eventosHistoricos(List.of()).build());
		assertFalse(alerta.isPresent());
	}

	@Test
	void anomalousTimePattern_disparaConRafagaDeAutenticaciones() {
		ReglaAntifraude regla = construirRegla(TipoRegla.ANOMALOUS_TIME_PATTERN, 55,
			Map.of("maxAuthInWindow", 5, "windowSeconds", 60), "BURST_AUTH");

		List<EventoVotacionDto> historicos = new ArrayList<>();
		for (int i = 1; i <= 8; i++) {
			historicos.add(autenticacion("MESA-9", "DOC-" + i, true, AHORA.minusSeconds(i * 5L)));
		}
		EventoVotacionDto actual = autenticacion("MESA-9", "DOC-NEW", true, AHORA);

		Optional<AlertaDto> alerta = new EvaluadorAnomalousTimePattern().evaluar(regla,
			VotingEventContext.builder().eventoActual(actual).eventosHistoricos(historicos).build());

		assertTrue(alerta.isPresent());
		assertEquals("MESA-9", alerta.get().getTableId());
		assertEquals(9L, alerta.get().getDetails().get("autenticacionesEnVentana"));
	}

	@Test
	void irregularTableBehavior_disparaSiVolumenSuperaUmbralRespectoMesasComparables() {
		ReglaAntifraude regla = construirRegla(TipoRegla.IRREGULAR_TABLE_BEHAVIOR, 65,
			Map.of("deviationThreshold", 1.5, "minComparableTables", 2), "TABLE_OUTLIER");

		Map<String, List<EventoVotacionDto>> porMesa = new LinkedHashMap<>();
		porMesa.put("MESA-A", autenticaciones("MESA-A", 30));
		porMesa.put("MESA-B", autenticaciones("MESA-B", 12));
		porMesa.put("MESA-C", autenticaciones("MESA-C", 8));

		EventoVotacionDto actual = autenticacion("MESA-A", "DOC-X", true, AHORA);
		VotingEventContext ctx = VotingEventContext.builder()
			.eventoActual(actual)
			.eventosHistoricos(new ArrayList<>())
			.eventosPorMesaDelPuesto(porMesa)
			.build();

		Optional<AlertaDto> alerta = new EvaluadorIrregularTableBehavior().evaluar(regla, ctx);

		assertTrue(alerta.isPresent());
		assertEquals("MESA-A", alerta.get().getTableId());
		assertEquals(30L, alerta.get().getDetails().get("autenticacionesMesa"));
		assertEquals(10.0, (double) alerta.get().getDetails().get("promedioComparables"), 0.0001);
	}

	@Test
	void irregularTableBehavior_noDisparaSiNoHaySuficientesMesasComparables() {
		ReglaAntifraude regla = construirRegla(TipoRegla.IRREGULAR_TABLE_BEHAVIOR, 65,
			Map.of("deviationThreshold", 1.5, "minComparableTables", 5), "TABLE_OUTLIER");

		Map<String, List<EventoVotacionDto>> porMesa = new HashMap<>();
		porMesa.put("MESA-A", autenticaciones("MESA-A", 30));
		porMesa.put("MESA-B", autenticaciones("MESA-B", 12));

		EventoVotacionDto actual = autenticacion("MESA-A", "DOC-X", true, AHORA);
		VotingEventContext ctx = VotingEventContext.builder()
			.eventoActual(actual)
			.eventosPorMesaDelPuesto(porMesa)
			.build();

		assertFalse(new EvaluadorIrregularTableBehavior().evaluar(regla, ctx).isPresent());
	}

	// ---------- helpers ----------

	private ReglaAntifraude construirRegla(TipoRegla tipo, int peso,
		Map<String, Object> parametros, String alertType) {
		ReglaAntifraude regla = new ReglaAntifraude();
		regla.setId(1L);
		regla.setName("Regla " + tipo.name());
		regla.setRuleType(tipo);
		regla.setIsActive(Boolean.TRUE);
		regla.setApprovalStatus(EstadoAprobacion.APPROVED);
		regla.setRiskScoreWeight(peso);
		regla.setAlertType(alertType);
		regla.setCreatedBy("tester");
		regla.setParameters(parametros);
		return regla;
	}

	private EventoVotacionDto autenticacion(String mesa, String doc, boolean exitoso, LocalDateTime ts) {
		return EventoVotacionDto.builder()
			.tableId(mesa)
			.documentId(doc)
			.tipo(TipoEventoVotacion.AUTENTICACION)
			.exitoso(exitoso)
			.coincidenciaBiometrica(exitoso)
			.timestamp(ts)
			.build();
	}

	private EventoVotacionDto autenticacionBiometrica(String mesa, String doc, boolean match, LocalDateTime ts) {
		return EventoVotacionDto.builder()
			.tableId(mesa)
			.documentId(doc)
			.tipo(TipoEventoVotacion.AUTENTICACION)
			.exitoso(match)
			.coincidenciaBiometrica(match)
			.timestamp(ts)
			.build();
	}

	private EventoVotacionDto voto(String mesa, String doc, boolean exitoso, LocalDateTime ts) {
		return EventoVotacionDto.builder()
			.tableId(mesa)
			.documentId(doc)
			.tipo(TipoEventoVotacion.VOTO)
			.exitoso(exitoso)
			.coincidenciaBiometrica(true)
			.timestamp(ts)
			.build();
	}

	private List<EventoVotacionDto> autenticaciones(String mesa, int cantidad) {
		List<EventoVotacionDto> lista = new ArrayList<>();
		for (int i = 0; i < cantidad; i++) {
			lista.add(autenticacion(mesa, "DOC-" + mesa + "-" + i, true, AHORA.minusMinutes(i)));
		}
		return lista;
	}
}
