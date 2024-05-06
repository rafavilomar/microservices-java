package com.microservice_level_up.grpc;

import com.google.protobuf.Empty;
import com.microservice_level_up.dto.BuyProductRequest;
import com.microservice_level_up.module.product.IProductService;
import common.grpc.common.ProductRequestByCode;
import common.grpc.common.ProductRequestById;
import common.grpc.common.ProductResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@GrpcService
public class ProductServiceGrpc extends common.grpc.common.ProductServiceGrpc.ProductServiceImplBase {

    private final IProductService productService;

    @Override
    public void buyProduct(common.grpc.common.BuyProductRequest request, StreamObserver<Empty> responseObserver){
        List<BuyProductRequest> products = request.getProductsList()
                .stream()
                .map(product -> new BuyProductRequest(product.getQuantity(), product.getCode(), 10))
                .toList();

        productService.buy(products);

        responseObserver.onNext(null);
        responseObserver.onCompleted();
    }

    @Override
    public void getProductById(ProductRequestById request, StreamObserver<ProductResponse> responseObserver) {
        com.microservice_level_up.dto.ProductResponse product = productService.getById(request.getId());

        responseObserver.onNext(ProductResponse.newBuilder()
                .setCode(product.code())
                .setPrice(product.price())
                .setName(product.name())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getProductByCode(ProductRequestByCode request, StreamObserver<ProductResponse> responseObserver) {
        com.microservice_level_up.dto.ProductResponse product = productService.getByCode(request.getCode());

        responseObserver.onNext(ProductResponse.newBuilder()
                .setCode(product.code())
                .setPrice(product.price())
                .setName(product.name())
                .build());
        responseObserver.onCompleted();
    }
}
