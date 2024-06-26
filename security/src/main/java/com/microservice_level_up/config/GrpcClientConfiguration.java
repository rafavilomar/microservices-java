package com.microservice_level_up.config;

import common.grpc.common.CustomerServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfiguration {

    @Bean
    CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceBlockingStub() {
        Channel channel = ManagedChannelBuilder.forAddress("localhost", 8082)
                .usePlaintext()
                .build();

        return CustomerServiceGrpc.newBlockingStub(channel);
    }
}
