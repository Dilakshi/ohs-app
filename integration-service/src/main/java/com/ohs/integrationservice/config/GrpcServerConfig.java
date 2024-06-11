package com.ohs.integrationservice.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Configuration
public class GrpcServerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerConfig.class);

    @Value("${grpc.integration-service.port}")
    private int integrationPort;

    private Server server;

    @PostConstruct
    public void startServer() throws IOException {
        server = ServerBuilder.forPort(integrationPort)
                .build()
                .start();
        LOGGER.info("Server started, listening on ", integrationPort);
    }

    @PreDestroy
    public void stopServer() {
        if (server != null) {
            server.shutdown();
            LOGGER.info("Server stopped on ", integrationPort);
        }
    }
}
