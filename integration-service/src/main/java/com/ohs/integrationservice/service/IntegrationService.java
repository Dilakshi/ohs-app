package com.ohs.integrationservice.service;

import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import com.ohs.integrationservice.grpc.OrderServiceClient;
import com.ohs.integrationservice.grpc.ProductServiceClient;
import com.ohs.integrationservice.grpc.UserServiceClient;
import com.ohs.integrationservice.model.OrderRecord;
import com.ohs.integrationservice.model.ProcessedOrder;
import com.ohs.integrationservice.util.JSONFileWriter;
import io.grpc.StatusRuntimeException;
import order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import product.Product;
import user.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class IntegrationService {

    private final UserServiceClient userServiceClient;
    private final OrderServiceClient orderServiceClient;
    private final ProductServiceClient productServiceClient;
    private final JSONFileWriter outputWriter;

    @Value("${processed.result.file.path}")
    private String outputFilePath;

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationService.class);

    private static final DateTimeFormatter INPUT_DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final DateTimeFormatter OUTPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public IntegrationService(OrderServiceClient orderServiceClient, UserServiceClient userServiceClient, ProductServiceClient productServiceClient, JSONFileWriter outputWriter) {
        this.orderServiceClient = orderServiceClient;
        this.userServiceClient = userServiceClient;
        this.productServiceClient = productServiceClient;
        this.outputWriter = outputWriter;
    }

    public ProcessedOrder processOrder(OrderRecord orderRecord){
        String userPid = createUser(orderRecord);
        if (userPid != null){
            OrderOutput output = createOrder(orderRecord, userPid);
            ProcessedOrder processedOrder = new ProcessedOrder(userPid, output.orderId, output.supplierPid);
            outputWriter.writeProcessOrdersToJson(outputFilePath, processedOrder);
            return processedOrder;
        }
        return null;
    }

    public String createUser(OrderRecord orderRecord){
        User.CreateUserRequest userRequest = User.CreateUserRequest.newBuilder()
                .setFullName(StringValue.of(orderRecord.getFullName()))
                .setEmail(orderRecord.getEmail())
                .setAddress(User.ShippingAddress.newBuilder()
                        .setAddress(StringValue.of(orderRecord.getShippingAddress()))
                        .setCountry(StringValue.of(orderRecord.getCountry()))
                        .build())
                .build();

        try {
            User.UserResponse userResponse = userServiceClient.createUser(userRequest);
            return userResponse.getPid();
        } catch (StatusRuntimeException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }


    private OrderOutput createOrder(OrderRecord order, String userPid) {
        Product.ProductResponse productResponse = productServiceClient.getProductByPid(StringValue.of(order.getProductPid()));
        Order.Product productDetails = Order.Product.newBuilder()
                .setPid(productResponse.getPid())
                .setPricePerUnit(productResponse.getPricePerUnit())
                .setQuantity(order.getQuantity())
                .build();

        Order.CreateOrderRequest orderRequest = Order.CreateOrderRequest.newBuilder()
                .addProducts(productDetails)
                .setUserPid(userPid)
                .setDateCreated(StringValue.of(order.getDateCreated())) //recheck conversion
                .setStatus(Order.OrderStatus.valueOf(String.valueOf(order.getOrderStatus())))
                .setPricePerUnit(productResponse.getPricePerUnit())
                .setQuantity(Integer.parseInt(String.valueOf(order.getQuantity())))
                .setDateDelivered(StringValue.of(LocalDate.now().format(OUTPUT_DATE_FORMATTER)))
                .build();

        orderServiceClient.createOrder(orderRequest);

        Order.PageableRequest requestPageable = Order.PageableRequest.newBuilder().setNumberPerPage(Int64Value.of(100)).build();
        Order.PageableResponse pageableResponse = orderServiceClient.getALlOrders(requestPageable);

        String supplierPid = order.getSupplierPid();
        String orderId = pageableResponse.getDataList().stream().filter(s -> s.getUserPid().equals(userPid))
                .findFirst()
                .map(Order.OrderResponse::getPid)
                .orElse(null);

        return new OrderOutput(supplierPid, orderId);
    }


    private record OrderOutput(String supplierPid, String orderId) {
    }
}
