package com.ohs.integrationservice.model;

import lombok.Data;

@Data
public class OrderRecord {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String supplierPid;
    private String creditCardNumber;
    private String creditCardType;
    private String orderId;
    private String productPid;
    private String shippingAddress;
    private String country;
    private String dateCreated;
    private int quantity;
    private String fullName;
    private String orderStatus;
}
