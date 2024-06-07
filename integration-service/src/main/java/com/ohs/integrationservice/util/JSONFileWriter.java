package com.ohs.integrationservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ohs.integrationservice.model.ProcessedOrder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class JSONFileWriter {

    public void writeProcessOrdersToJson(String filePath, ProcessedOrder processedOrder) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

        try {
            writer.writeValue(new File(filePath), processedOrder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

