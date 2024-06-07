package com.ohs.integrationservice.processor;

import com.ohs.integrationservice.model.OrderData;

import com.ohs.integrationservice.service.IntegrationService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class OrderDataProcessor implements ItemProcessor<OrderData, OrderData> {

    private IntegrationService integrationService;

    public OrderDataProcessor(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @Override
    public OrderData process(OrderData orderData){
        integrationService.processOrder(orderData);
        return orderData;
    }


}
