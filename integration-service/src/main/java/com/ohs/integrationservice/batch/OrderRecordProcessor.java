package com.ohs.integrationservice.batch;

import com.ohs.integrationservice.model.OrderRecord;
import com.ohs.integrationservice.model.ProcessedOrder;
import com.ohs.integrationservice.service.IntegrationService;
import org.springframework.batch.item.ItemProcessor;

public class OrderRecordProcessor implements ItemProcessor<OrderRecord, ProcessedOrder> {

    private final IntegrationService integrationService;

    public OrderRecordProcessor(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @Override
    public ProcessedOrder process(OrderRecord orderRecord){
        return integrationService.processOrder(orderRecord);
    }

}
