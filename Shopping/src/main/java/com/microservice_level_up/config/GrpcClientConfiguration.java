package com.microservice_level_up.config;

import common.grpc.common.CustomerServiceGrpc;
import common.grpc.common.LoyaltyServiceGrpc;
import common.grpc.common.ProductServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfiguration {

    @Bean
    CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceBlockingStub() {
        Channel channel = ManagedChannelBuilder.forAddress("localhost", 8081)
                .usePlaintext()
                .build();

        return CustomerServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub() {
        Channel channel = ManagedChannelBuilder.forAddress("localhost", 8150)
                .usePlaintext()
                .build();

        return ProductServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    LoyaltyServiceGrpc.LoyaltyServiceBlockingStub loyaltyServiceBlockingStub() {
        Channel channel = ManagedChannelBuilder.forAddress("localhost", 8095)
                .usePlaintext()
                .build();

        return LoyaltyServiceGrpc.newBlockingStub(channel);
    }
}
