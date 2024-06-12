package com.ohs.integrationservice.batch;

import com.ohs.integrationservice.model.ProcessedOrder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class OrderRecordWriter implements ItemWriter<ProcessedOrder> {

    @Override
    public void write(Chunk<? extends ProcessedOrder> chunk) throws Exception {

    }
}

