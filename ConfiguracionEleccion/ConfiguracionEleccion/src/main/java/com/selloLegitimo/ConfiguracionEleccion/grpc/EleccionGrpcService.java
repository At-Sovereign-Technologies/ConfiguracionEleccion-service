package com.selloLegitimo.ConfiguracionEleccion.grpc;

import com.selloLegitimo.ConfiguracionEleccion.modelo.Eleccion;
import com.selloLegitimo.ConfiguracionEleccion.repositorio.RepositorioEleccion;
import com.selloLegitimo.grpc.elecciones.EleccionDetalle;
import com.selloLegitimo.grpc.elecciones.EleccionResumen;
import com.selloLegitimo.grpc.elecciones.EleccionServiceGrpc;
import com.selloLegitimo.grpc.elecciones.ListarEleccionesRequest;
import com.selloLegitimo.grpc.elecciones.ListarEleccionesResponse;
import com.selloLegitimo.grpc.elecciones.ObtenerEleccionRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EleccionGrpcService extends EleccionServiceGrpc.EleccionServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(EleccionGrpcService.class);

    @Autowired
    private RepositorioEleccion repositorioEleccion;

    @Override
    public void listarElecciones(ListarEleccionesRequest request,
                                 StreamObserver<ListarEleccionesResponse> responseObserver) {
        logger.info("gRPC: listarElecciones solicitado");
        try {
            ListarEleccionesResponse.Builder builder = ListarEleccionesResponse.newBuilder();
            for (Eleccion e : repositorioEleccion.findAll()) {
                builder.addElecciones(EleccionResumen.newBuilder()
                        .setId(e.getId())
                        .setNombreOficial(e.getNombreOficial() != null ? e.getNombreOficial() : "")
                        .setEstado(e.getEstado() != null ? e.getEstado().name() : "")
                        .build());
            }
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            logger.error("gRPC listarElecciones error", ex);
            responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void obtenerEleccion(ObtenerEleccionRequest request,
                                StreamObserver<EleccionDetalle> responseObserver) {
        logger.info("gRPC: obtenerEleccion id={}", request.getId());
        try {
            Eleccion e = repositorioEleccion.findById(request.getId()).orElse(null);
            if (e == null) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("No existe eleccion con id " + request.getId())
                        .asRuntimeException());
                return;
            }
            EleccionDetalle.Builder detalleBuilder = EleccionDetalle.newBuilder()
                    .setId(e.getId())
                    .setNombreOficial(e.getNombreOficial() != null ? e.getNombreOficial() : "")
                    .setEstado(e.getEstado() != null ? e.getEstado().name() : "")
                    .setFechaInicioJornada(e.getFechaInicioJornada() != null ? e.getFechaInicioJornada().toString() : "")
                    .setFechaCierreJornada(e.getFechaCierreJornada() != null ? e.getFechaCierreJornada().toString() : "")
                    .setDocumentoNoVotable(e.getDocumentoNoVotable() != null ? e.getDocumentoNoVotable() : "");
            e.getExcencionesHabilitadas().forEach(detalleBuilder::addExcencionesHabilitadas);
            EleccionDetalle detalle = detalleBuilder.build();
            responseObserver.onNext(detalle);
            responseObserver.onCompleted();
        } catch (Exception ex) {
            logger.error("gRPC obtenerEleccion error", ex);
            responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage()).asRuntimeException());
        }
    }
}
