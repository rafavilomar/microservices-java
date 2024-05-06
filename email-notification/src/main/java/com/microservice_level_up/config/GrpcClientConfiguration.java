package com.microservice_level_up.config;

import common.grpc.common.ProductServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfiguration {

    @Bean
    ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub() {
        Channel channel = ManagedChannelBuilder.forAddress("localhost", 8081)
                .usePlaintext()
                .build();

        return ProductServiceGrpc.newBlockingStub(channel);
    }
}
