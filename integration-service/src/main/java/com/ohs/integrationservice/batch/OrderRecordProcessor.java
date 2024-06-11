package com.ohs.integrationservice.batch;

import com.ohs.integrationservice.model.OrderRecord;
import com.ohs.integrationservice.service.IntegrationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderRecordProcessor implements ItemProcessor<OrderRecord, OrderRecord> {

    private IntegrationService integrationService;

    @Override
    public OrderRecord process(OrderRecord orderRecord){
        integrationService.processOrder(orderRecord);
        return orderRecord;
    }

}
