package com.ohs.integrationservice.model;

import lombok.Data;

@Data
public class ProcessedOrder {
    private String userPid;
    private String orderPid;
    private String supplierPid;

    public ProcessedOrder(String userPid, String orderPid, String supplierPid) {
        this.userPid = userPid;
        this.orderPid = orderPid;
        this.supplierPid = supplierPid;
    }
}

