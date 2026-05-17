package com.selloLegitimo.ConfiguracionEleccion.servicio;

// ============================================================
//  TIPO: Unitaria — ServicioEleccion
//  Verifica: Lógica de creación de elecciones y validación de reglas de negocio.
//
//  NOTA PARA DESARROLLADORES:
//  Los casos TC-CE-002, TC-CE-003 y TC-CE-004 validan la regla de negocio
//  RN-02 (la jornada electoral debe durar exactamente 48 horas).
//  Actualmente el código en ServicioEleccion.validarFechas() no implementa
//  esta validación, por lo que estos tests fallarán hasta que se agregue
//  dicha validación en el código de producción.
// ============================================================

import com.selloLegitimo.ConfiguracionEleccion.dto.RespuestaEleccion;
import com.selloLegitimo.ConfiguracionEleccion.dto.SolicitudCrearEleccion;
import com.selloLegitimo.ConfiguracionEleccion.excepcion.ExcepcionReglaNegocio;
import com.selloLegitimo.ConfiguracionEleccion.modelo.CodigoMetodoElectoral;
import com.selloLegitimo.ConfiguracionEleccion.modelo.Eleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.EstadoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.ModalidadHabilitada;
import com.selloLegitimo.ConfiguracionEleccion.modelo.ModeloCandidatura;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoCircunscripcion;
import com.selloLegitimo.ConfiguracionEleccion.modelo.TipoEleccion;
import com.selloLegitimo.ConfiguracionEleccion.repositorio.RepositorioEleccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServicioEleccion — Pruebas Unitarias de Reglas de Negocio")
class ServicioEleccionTest {

    @Mock
    private RepositorioEleccion repositorioEleccion;

    @InjectMocks
    private ServicioEleccion servicioEleccion;

    private SolicitudCrearEleccion solicitudValida;

    @BeforeEach
    void setUp() {
        solicitudValida = new SolicitudCrearEleccion();
        solicitudValida.setNombreOficial("Elecciones Presidenciales 2026");
        solicitudValida.setPais("Colombia");
        solicitudValida.setTipoEleccion(TipoEleccion.PRESIDENCIAL);
        solicitudValida.setCodigoMetodoElectoral(CodigoMetodoElectoral.ME_01);
        solicitudValida.setFechaInicioJornada(LocalDateTime.of(2026, 5, 29, 8, 0));
        solicitudValida.setFechaCierreJornada(LocalDateTime.of(2026, 5, 31, 8, 0)); // 48 horas exactas
        solicitudValida.setModalidadHabilitada(ModalidadHabilitada.AMBAS);
        solicitudValida.setTipoCircunscripcion(TipoCircunscripcion.TERRITORIAL);
        solicitudValida.setDocumentoNoVotable("N/A");
        solicitudValida.setEstado(EstadoEleccion.BORRADOR);
    }

    // ------------------------------------------------------------------
    // TC-CE-001 | Duración exacta de 48 horas (RN-02)
    // ------------------------------------------------------------------
    @Test
    @DisplayName("TC-CE-001 | RN-02 | Duración exacta de 48 horas → Creada exitosamente")
    void tc_ce_001_duracion_exacta_48_horas_crea_eleccion() {
        Eleccion eleccionGuardada = new Eleccion();
        eleccionGuardada.setId(1L);
        eleccionGuardada.setNombreOficial(solicitudValida.getNombreOficial());
        eleccionGuardada.setPais(solicitudValida.getPais());
        eleccionGuardada.setTipoEleccion(solicitudValida.getTipoEleccion());
        eleccionGuardada.setCodigoMetodoElectoral(solicitudValida.getCodigoMetodoElectoral());
        eleccionGuardada.setFechaInicioJornada(solicitudValida.getFechaInicioJornada());
        eleccionGuardada.setFechaCierreJornada(solicitudValida.getFechaCierreJornada());
        eleccionGuardada.setModalidadHabilitada(solicitudValida.getModalidadHabilitada());
        eleccionGuardada.setTipoCircunscripcion(solicitudValida.getTipoCircunscripcion());
        eleccionGuardada.setDocumentoNoVotable(solicitudValida.getDocumentoNoVotable());
        eleccionGuardada.setEstado(EstadoEleccion.BORRADOR);

        when(repositorioEleccion.save(any(Eleccion.class))).thenReturn(eleccionGuardada);

        RespuestaEleccion respuesta = servicioEleccion.crearEleccion(solicitudValida);

        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getId()).isEqualTo(1L);
        assertThat(respuesta.getFechaInicioJornada()).isEqualTo(solicitudValida.getFechaInicioJornada());
        assertThat(respuesta.getFechaCierreJornada()).isEqualTo(solicitudValida.getFechaCierreJornada());
    }

    // ------------------------------------------------------------------
    // TC-CE-002 | Duración de 24 horas (Rechazada - RN-02)
    // ------------------------------------------------------------------
    @Test
    @DisplayName("TC-CE-002 | RN-02 | Duración de 24 horas → Lanzar ExcepcionReglaNegocio")
    void tc_ce_002_duracion_24_horas_rechazada() {
        solicitudValida.setFechaCierreJornada(solicitudValida.getFechaInicioJornada().plusHours(24));

        assertThrows(ExcepcionReglaNegocio.class, () -> {
            servicioEleccion.crearEleccion(solicitudValida);
        }, "Se esperaba ExcepcionReglaNegocio debido a que la jornada debe ser de exactamente 48 horas");
    }

    // ------------------------------------------------------------------
    // TC-CE-003 | Duración de 47 horas (Rechazada - RN-02)
    // ------------------------------------------------------------------
    @Test
    @DisplayName("TC-CE-003 | RN-02 | Duración de 47 horas → Lanzar ExcepcionReglaNegocio")
    void tc_ce_003_duracion_47_horas_rechazada() {
        solicitudValida.setFechaCierreJornada(solicitudValida.getFechaInicioJornada().plusHours(47));

        assertThrows(ExcepcionReglaNegocio.class, () -> {
            servicioEleccion.crearEleccion(solicitudValida);
        }, "Se esperaba ExcepcionReglaNegocio debido a que la jornada debe ser de exactamente 48 horas");
    }

    // ------------------------------------------------------------------
    // TC-CE-004 | Duración de 49 horas (Rechazada - RN-02)
    // ------------------------------------------------------------------
    @Test
    @DisplayName("TC-CE-004 | RN-02 | Duración de 49 horas → Lanzar ExcepcionReglaNegocio")
    void tc_ce_004_duracion_49_horas_rechazada() {
        solicitudValida.setFechaCierreJornada(solicitudValida.getFechaInicioJornada().plusHours(49));

        assertThrows(ExcepcionReglaNegocio.class, () -> {
            servicioEleccion.crearEleccion(solicitudValida);
        }, "Se esperaba ExcepcionReglaNegocio debido a que la jornada debe ser de exactamente 48 horas");
    }

    // ------------------------------------------------------------------
    // TC-CE-005 | Cierre antes de Inicio (Rechazada)
    // ------------------------------------------------------------------
    @Test
    @DisplayName("TC-CE-005 | Cierre antes de Inicio → Lanzar ExcepcionReglaNegocio")
    void tc_ce_005_cierre_anterior_inicio_rechazada() {
        solicitudValida.setFechaCierreJornada(solicitudValida.getFechaInicioJornada().minusDays(1));

        assertThatThrownBy(() -> servicioEleccion.crearEleccion(solicitudValida))
                .isInstanceOf(ExcepcionReglaNegocio.class)
                .hasMessageContaining("La fecha de cierre no puede ser anterior");
    }

    // ------------------------------------------------------------------
    // TC-CE-006 | Cierre igual a Inicio (Rechazada)
    // ------------------------------------------------------------------
    @Test
    @DisplayName("TC-CE-006 | Cierre igual a Inicio → Lanzar ExcepcionReglaNegocio")
    void tc_ce_006_cierre_igual_inicio_rechazada() {
        solicitudValida.setFechaCierreJornada(solicitudValida.getFechaInicioJornada());

        assertThatThrownBy(() -> servicioEleccion.crearEleccion(solicitudValida))
                .isInstanceOf(ExcepcionReglaNegocio.class)
                .hasMessageContaining("La fecha de cierre debe ser posterior");
    }

    // ------------------------------------------------------------------
    // TC-CE-008 | ME-03 Proporcional sin Curules (Rechazada)
    // ------------------------------------------------------------------
    @Test
    @DisplayName("TC-CE-008 | ME-03 Proporcional sin Curules → Lanzar ExcepcionReglaNegocio")
    void tc_ce_008_me03_sin_curules_rechazada() {
        solicitudValida.setCodigoMetodoElectoral(CodigoMetodoElectoral.ME_03);
        solicitudValida.setNumeroCurules(null);

        assertThatThrownBy(() -> servicioEleccion.crearEleccion(solicitudValida))
                .isInstanceOf(ExcepcionReglaNegocio.class)
                .hasMessageContaining("ME-03 exige un numero de curules valido");
    }

    // ------------------------------------------------------------------
    // TC-CE-009 | ME-03 con Candidato UNICO (Rechazada)
    // ------------------------------------------------------------------
    @Test
    @DisplayName("TC-CE-009 | ME-03 con Modelo UNICO → Lanzar ExcepcionReglaNegocio")
    void tc_ce_009_me03_con_modelo_unico_rechazada() {
        solicitudValida.setCodigoMetodoElectoral(CodigoMetodoElectoral.ME_03);
        solicitudValida.setNumeroCurules(5);
        solicitudValida.setModelosCandidatura(List.of(ModeloCandidatura.UNICO));

        assertThatThrownBy(() -> servicioEleccion.crearEleccion(solicitudValida))
                .isInstanceOf(ExcepcionReglaNegocio.class)
                .hasMessageContaining("El metodo proporcional (ME-03) no admite candidato unico");
    }

    // ------------------------------------------------------------------
    // TC-CE-010 | ME-01 con Listas Abierta/Cerrada (Rechazada)
    // ------------------------------------------------------------------
    @Test
    @DisplayName("TC-CE-010 | ME-01 con Modelo ABIERTA → Lanzar ExcepcionReglaNegocio")
    void tc_ce_010_me01_con_modelo_abierta_rechazada() {
        solicitudValida.setCodigoMetodoElectoral(CodigoMetodoElectoral.ME_01);
        solicitudValida.setModelosCandidatura(List.of(ModeloCandidatura.ABIERTA));

        assertThatThrownBy(() -> servicioEleccion.crearEleccion(solicitudValida))
                .isInstanceOf(ExcepcionReglaNegocio.class)
                .hasMessageContaining("Las listas de partido (abierta/cerrada) solo aplican para el metodo proporcional");
    }
}
