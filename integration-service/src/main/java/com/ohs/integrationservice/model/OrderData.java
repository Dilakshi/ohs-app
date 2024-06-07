package com.ohs.integrationservice.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class OrderData {
    @CsvBindByName(column = "id")
    private String id;

    @CsvBindByName(column = "first_name")
    private String firstName;

    @CsvBindByName(column = "last_name")
    private String lastName;

    @CsvBindByName(column = "email")
    private String email;

    @CsvBindByName(column = "supplier_pid")
    private String supplierPid;

    @CsvBindByName(column = "credit_card_number")
    private String creditCardNumber;

    @CsvBindByName(column = "credit_card_type")
    private String creditCardType;

    @CsvBindByName(column = "order_id")
    private String orderId;

    @CsvBindByName(column = "product_pid")
    private String productPid;

    @CsvBindByName(column = "shipping_address")
    private String shippingAddress;

    @CsvBindByName(column = "country")
    private String country;

    @CsvBindByName(column = "date_created")
    private String dateCreated;

    @CsvBindByName(column = "quantity")
    private int quantity;

    @CsvBindByName(column = "full_name")
    private String fullName;

    @CsvBindByName(column = "order_status")
    private String orderStatus;
}
