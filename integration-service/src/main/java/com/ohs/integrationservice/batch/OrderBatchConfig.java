package com.ohs.integrationservice.config;

import com.ohs.integrationservice.model.OrderData;
import com.ohs.integrationservice.processor.OrderDataProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@EnableBatchProcessing
public class OrderBatchConfig {

    @Value("classpath:order-integration.csv")
    private Resource inputCsvFilePath;

    @Bean
    public FlatFileItemReader<OrderData> reader() {
        FlatFileItemReader<OrderData> reader = new FlatFileItemReader<>();
        reader.setResource(inputCsvFilePath);
        reader.setLineMapper(new DefaultLineMapper<OrderData>() {{
            setLineTokenizer(orderDataLineTokenizer());
            setFieldSetMapper(new BeanWrapperFieldSetMapper<OrderData>() {{
                setTargetType(OrderData.class);
            }});
        }});
        return reader;
    }

    @Bean
    public LineTokenizer orderDataLineTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        //TODO improve here
        tokenizer.setNames("id", "first_name", "last_name", "email", "supplier_pid", "credit_card_number",
                "credit_card_type", "order_id", "product_pid", "shipping_address", "country",
                "date_created", "quantity", "full_name", "order_status");
        return tokenizer;
    }


}
