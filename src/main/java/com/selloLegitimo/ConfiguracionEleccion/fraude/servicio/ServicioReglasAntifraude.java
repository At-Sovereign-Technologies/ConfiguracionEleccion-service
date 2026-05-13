package com.selloLegitimo.ConfiguracionEleccion.fraude.servicio;

import com.selloLegitimo.ConfiguracionEleccion.excepcion.ExcepcionRecursoNoEncontrado;
import com.selloLegitimo.ConfiguracionEleccion.excepcion.ExcepcionReglaNegocio;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.RespuestaReglaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.SolicitudCrearReglaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.dto.SolicitudEditarReglaDto;
import com.selloLegitimo.ConfiguracionEleccion.fraude.excepcion.ExcepcionEstadoReglaInvalido;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.AccionAuditoria;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.EstadoAprobacion;
import com.selloLegitimo.ConfiguracionEleccion.fraude.modelo.ReglaAntifraude;
import com.selloLegitimo.ConfiguracionEleccion.fraude.repositorio.RepositorioReglaAntifraude;
import com.selloLegitimo.ConfiguracionEleccion.fraude.seguridad.UsuarioContexto;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioReglasAntifraude implements IServicioReglasAntifraude {

	private static final Logger logger = LoggerFactory.getLogger(ServicioReglasAntifraude.class);

	@Autowired
	private RepositorioReglaAntifraude repositorioReglas;

	@Autowired
	private ServicioAuditoriaRegla servicioAuditoria;

	@Override
	@Transactional(readOnly = true)
	public List<RespuestaReglaDto> listar() {
		return repositorioReglas.findAll().stream()
			.map(this::convertirARespuesta)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public RespuestaReglaDto obtenerPorId(Long id) {
		return convertirARespuesta(buscar(id));
	}

	@Override
	@Transactional
	public RespuestaReglaDto crear(SolicitudCrearReglaDto solicitud, UsuarioContexto usuario) {
		logger.info("Creando regla antifraude '{}' tipo {} por usuario {}",
			solicitud.getName(), solicitud.getRuleType(), usuario.getUsuario());

		repositorioReglas.findByNameIgnoreCase(solicitud.getName().trim()).ifPresent(existente -> {
			throw new ExcepcionReglaNegocio("Ya existe una regla con el nombre '" + solicitud.getName() + "'");
		});

		ReglaAntifraude regla = new ReglaAntifraude();
		regla.setName(solicitud.getName().trim());
		regla.setDescription(solicitud.getDescription());
		regla.setRuleType(solicitud.getRuleType());
		regla.setIsActive(solicitud.getIsActive());
		regla.setParameters(solicitud.getParameters());
		regla.setAlertType(solicitud.getAlertType().trim());
		regla.setRiskScoreWeight(solicitud.getRiskScoreWeight());
		regla.setApprovalStatus(EstadoAprobacion.PENDING);
		regla.setCreatedBy(usuario.getUsuario());

		ReglaAntifraude guardada = repositorioReglas.save(regla);
		servicioAuditoria.registrar(guardada, AccionAuditoria.CREAR, null, EstadoAprobacion.PENDING,
			usuario.getUsuario(), usuario.getRol(), null);

		return convertirARespuesta(guardada);
	}

	@Override
	@Transactional
	public RespuestaReglaDto editar(Long id, SolicitudEditarReglaDto solicitud, UsuarioContexto usuario) {
		ReglaAntifraude regla = buscar(id);
		if (regla.getDeletedAt() != null) {
			throw new ExcepcionEstadoReglaInvalido("No se puede editar una regla desactivada");
		}

		EstadoAprobacion estadoAnterior = regla.getApprovalStatus();

		if (solicitud.getDescription() != null) {
			regla.setDescription(solicitud.getDescription());
		}
		if (solicitud.getIsActive() != null) {
			regla.setIsActive(solicitud.getIsActive());
		}
		if (solicitud.getParameters() != null) {
			regla.setParameters(solicitud.getParameters());
		}
		if (solicitud.getAlertType() != null) {
			regla.setAlertType(solicitud.getAlertType().trim());
		}
		if (solicitud.getRiskScoreWeight() != null) {
			regla.setRiskScoreWeight(solicitud.getRiskScoreWeight());
		}

		// Toda edicion vuelve la regla al estado PENDING y requiere nueva aprobacion.
		regla.setApprovalStatus(EstadoAprobacion.PENDING);
		regla.setApprovedAt(null);
		regla.setApprovedBy(null);
		regla.setRejectionReason(null);

		ReglaAntifraude guardada = repositorioReglas.save(regla);
		servicioAuditoria.registrar(guardada, AccionAuditoria.EDITAR, estadoAnterior, EstadoAprobacion.PENDING,
			usuario.getUsuario(), usuario.getRol(), null);

		return convertirARespuesta(guardada);
	}

	@Override
	@Transactional
	public RespuestaReglaDto aprobar(Long id, UsuarioContexto usuario) {
		ReglaAntifraude regla = buscar(id);
		if (regla.getApprovalStatus() != EstadoAprobacion.PENDING) {
			throw new ExcepcionEstadoReglaInvalido("Solo se pueden aprobar reglas en estado PENDING");
		}

		EstadoAprobacion estadoAnterior = regla.getApprovalStatus();
		regla.setApprovalStatus(EstadoAprobacion.APPROVED);
		regla.setApprovedBy(usuario.getUsuario());
		regla.setApprovedAt(LocalDateTime.now());
		regla.setRejectionReason(null);

		ReglaAntifraude guardada = repositorioReglas.save(regla);
		servicioAuditoria.registrar(guardada, AccionAuditoria.APROBAR, estadoAnterior, EstadoAprobacion.APPROVED,
			usuario.getUsuario(), usuario.getRol(), null);

		return convertirARespuesta(guardada);
	}

	@Override
	@Transactional
	public RespuestaReglaDto rechazar(Long id, String motivo, UsuarioContexto usuario) {
		ReglaAntifraude regla = buscar(id);
		if (regla.getApprovalStatus() != EstadoAprobacion.PENDING) {
			throw new ExcepcionEstadoReglaInvalido("Solo se pueden rechazar reglas en estado PENDING");
		}

		EstadoAprobacion estadoAnterior = regla.getApprovalStatus();
		regla.setApprovalStatus(EstadoAprobacion.REJECTED);
		regla.setRejectionReason(motivo);
		regla.setApprovedAt(null);
		regla.setApprovedBy(null);

		ReglaAntifraude guardada = repositorioReglas.save(regla);
		servicioAuditoria.registrar(guardada, AccionAuditoria.RECHAZAR, estadoAnterior, EstadoAprobacion.REJECTED,
			usuario.getUsuario(), usuario.getRol(), motivo);

		return convertirARespuesta(guardada);
	}

	@Override
	@Transactional
	public RespuestaReglaDto desactivar(Long id, UsuarioContexto usuario) {
		ReglaAntifraude regla = buscar(id);
		if (regla.getDeletedAt() != null) {
			throw new ExcepcionEstadoReglaInvalido("La regla ya esta desactivada");
		}

		EstadoAprobacion estadoAnterior = regla.getApprovalStatus();
		regla.setIsActive(false);
		regla.setDeletedAt(LocalDateTime.now());
		// La desactivacion requiere aprobacion CNE, asi que la regla queda con estado
		// APPROVED solo si quien desactiva es CNE; el control de rol esta en el endpoint.
		regla.setApprovalStatus(EstadoAprobacion.APPROVED);
		regla.setApprovedBy(usuario.getUsuario());
		regla.setApprovedAt(LocalDateTime.now());

		ReglaAntifraude guardada = repositorioReglas.save(regla);
		servicioAuditoria.registrar(guardada, AccionAuditoria.DESACTIVAR, estadoAnterior, EstadoAprobacion.APPROVED,
			usuario.getUsuario(), usuario.getRol(), "Desactivacion (soft delete) aprobada");

		return convertirARespuesta(guardada);
	}

	private ReglaAntifraude buscar(Long id) {
		return repositorioReglas.findById(id)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("No existe la regla antifraude con id " + id));
	}

	private RespuestaReglaDto convertirARespuesta(ReglaAntifraude regla) {
		RespuestaReglaDto respuesta = new RespuestaReglaDto();
		respuesta.setId(regla.getId());
		respuesta.setName(regla.getName());
		respuesta.setDescription(regla.getDescription());
		respuesta.setRuleType(regla.getRuleType());
		respuesta.setIsActive(regla.getIsActive());
		respuesta.setParameters(regla.getParameters());
		respuesta.setAlertType(regla.getAlertType());
		respuesta.setRiskScoreWeight(regla.getRiskScoreWeight());
		respuesta.setApprovalStatus(regla.getApprovalStatus());
		respuesta.setCreatedBy(regla.getCreatedBy());
		respuesta.setApprovedBy(regla.getApprovedBy());
		respuesta.setRejectionReason(regla.getRejectionReason());
		respuesta.setCreatedAt(regla.getCreatedAt());
		respuesta.setUpdatedAt(regla.getUpdatedAt());
		respuesta.setApprovedAt(regla.getApprovedAt());
		respuesta.setDeletedAt(regla.getDeletedAt());
		return respuesta;
	}
}
