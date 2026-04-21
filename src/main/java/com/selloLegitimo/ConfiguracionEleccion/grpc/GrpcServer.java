package com.selloLegitimo.ConfiguracionEleccion.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GrpcServer {

    private static final Logger logger = LoggerFactory.getLogger(GrpcServer.class);

    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    @Autowired
    private EleccionGrpcService eleccionGrpcService;

    private Server server;

    @PostConstruct
    public void start() throws Exception {
        server = ServerBuilder.forPort(grpcPort)
                .addService(eleccionGrpcService)
                .build()
                .start();
        logger.info("Servidor gRPC iniciado en puerto {}", grpcPort);
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            logger.info("Deteniendo servidor gRPC...");
            server.shutdown();
        }
    }
}
