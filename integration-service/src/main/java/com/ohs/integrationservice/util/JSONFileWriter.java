package com.ohs.integrationservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ohs.integrationservice.model.ProcessedOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class JSONFileWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONFileWriter.class);

    public void writeProcessOrdersToJson(String filePath, ProcessedOrder processedOrder) {
        if (filePath == null || filePath.isEmpty()) {
            LOGGER.error("File path is null or empty");
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        if (processedOrder == null) {
            LOGGER.error("Processed order is null");
            throw new IllegalArgumentException("Processed order cannot be null");
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

        try {
            File file = new File(filePath);

            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            writer.writeValue(file, processedOrder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

