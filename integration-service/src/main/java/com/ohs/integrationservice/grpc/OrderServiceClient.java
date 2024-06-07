package com.ohs.integrationservice.grpc;

import com.ohs.integrationservice.model.OrderData;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import order.OrderServiceGrpc;
import order.Order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceClient {

    private final OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub;

    public OrderServiceClient(@Value("${grpc.order-service.host}") String host,
                              @Value("${grpc.order-service.port}") int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        orderServiceStub = OrderServiceGrpc.newBlockingStub(channel);
    }

    public Order.OrderResponse createOrder(Order.CreateOrderRequest request) {
        return orderServiceStub.createOrder(request);
    }

    public Order.PageableResponse getALlOrders(Order.PageableRequest request){
        return  orderServiceStub.getAllOrdersPageable(request);
    }
}
