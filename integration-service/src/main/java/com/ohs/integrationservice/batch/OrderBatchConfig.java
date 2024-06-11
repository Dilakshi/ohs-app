package com.ohs.integrationservice.batch;

import com.ohs.integrationservice.model.OrderRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class OrderBatchConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBatchConfig.class);

    @Value("${order.csv.file.path}")
    private Resource inputCsvFilePath;

    @Value("${processed.result.file.path}")
    private String outputJsonFilePath;

    @Value("${batch.chunk.size}")
    private int batchSize;

    @Bean
    public FlatFileItemReader<OrderRecord> reader() {
        if (!inputCsvFilePath.exists()) {
            throw new IllegalArgumentException("Input order CSV file does not exist: " + inputCsvFilePath);
        }
        LOGGER.info("Reading the order CSV..... Input file path: {}", inputCsvFilePath);

        FlatFileItemReader<OrderRecord> reader = new FlatFileItemReader<>();
        try {
            reader.setResource(inputCsvFilePath);
            reader.setLinesToSkip(1);
            reader.setLineMapper(new DefaultLineMapper<OrderRecord>() {{
                setLineTokenizer(orderDataLineTokenizer());
                setFieldSetMapper(new BeanWrapperFieldSetMapper<OrderRecord>() {{
                    setTargetType(OrderRecord.class);
                }});
            }});
        } catch (Exception e){
            LOGGER.error(e.getMessage());
        }

        return reader;
    }

    @Bean
    public LineTokenizer orderDataLineTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("id", "first_name", "last_name", "email", "supplier_pid", "credit_card_number",
                "credit_card_type", "order_id", "product_pid", "shipping_address", "country",
                "date_created", "quantity", "full_name", "order_status");
        return tokenizer;
    }


    @Bean
    public OrderRecordProcessor processor(){
        return new OrderRecordProcessor();
    }

    @Bean
    public OrderRecordWriter writer() {
        return new OrderRecordWriter();
    }

    @Bean
    public Step step(ItemReader<OrderRecord> reader, ItemProcessor<OrderRecord, OrderRecord> processor,
                     ItemWriter<OrderRecord> writer, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("orderStep",jobRepository)
                .<OrderRecord, OrderRecord>chunk(batchSize,transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job job(Step step,JobRepository jobRepository) {
        return new JobBuilder("orderJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(step)
                .end()
                .build();
    }

}
