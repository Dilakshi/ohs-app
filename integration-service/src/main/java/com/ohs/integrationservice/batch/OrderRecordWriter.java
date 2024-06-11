package com.ohs.integrationservice.batch;

import com.ohs.integrationservice.model.OrderRecord;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class OrderRecordWriter implements ItemWriter<OrderRecord> {

    @Override
    public void write(Chunk<? extends OrderRecord> chunk) throws Exception {

    }
}

