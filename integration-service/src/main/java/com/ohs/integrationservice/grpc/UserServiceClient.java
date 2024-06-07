package com.ohs.integrationservice.grpc;

import com.ohs.integrationservice.exception.UserValidationException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import user.UserServiceGrpc;
import user.User;

import org.springframework.stereotype.Service;

@Service
public class UserServiceClient {

    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    public UserServiceClient(@Value("${grpc.user-service.host}") String host,
                             @Value("${grpc.user-service.port}") int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        userServiceBlockingStub = UserServiceGrpc.newBlockingStub(channel);
    }

    public User.UserResponse createUser(User.CreateUserRequest request) {
        try {
            return userServiceBlockingStub.createUser(request);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.ALREADY_EXISTS) {
                throw new UserValidationException("User already exists with given email: " + request.getEmail());
            }
            throw e;
        }
    }
}

