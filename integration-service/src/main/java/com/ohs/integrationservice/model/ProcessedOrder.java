package com.ohs.integrationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessedOrder {
    private String userPid;
    private String orderPid;
    private String supplierPid;
}

