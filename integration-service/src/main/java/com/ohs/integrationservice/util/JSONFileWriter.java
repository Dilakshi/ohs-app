package com.ohs.integrationservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ohs.integrationservice.model.ProcessedOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
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
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectWriter writer = mapper.writer();

        try (FileWriter fileWriter = new FileWriter(filePath, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            writer.writeValue(bufferedWriter, processedOrder);
            bufferedWriter.newLine();
        } catch (IOException e) {
            LOGGER.error("Error writing to file: {}", e.getMessage());
        }
    }
}

