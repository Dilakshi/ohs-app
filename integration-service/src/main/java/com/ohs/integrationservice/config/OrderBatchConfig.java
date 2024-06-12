package com.ohs.integrationservice.config;

import com.ohs.integrationservice.batch.OrderRecordProcessor;
import com.ohs.integrationservice.batch.OrderRecordWriter;
import com.ohs.integrationservice.model.OrderRecord;
import com.ohs.integrationservice.model.ProcessedOrder;
import com.ohs.integrationservice.service.IntegrationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
//@EnableBatchProcessing
public class OrderBatchConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBatchConfig.class);

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final IntegrationService integrationService;

    @Value("${order.csv.file.path}")
    private Resource inputCsvFilePath;

    @Value("${processed.result.file.path}")
    private String outputJsonFilePath;

    @Value("${batch.chunk.size}")
    private int batchSize;

    @Value("${concurrent.tasks}")
    private int concurrencyLimit;

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
            reader.setLineMapper(lineMapper());
        } catch (Exception e){
            LOGGER.error(e.getMessage());
        }

        return reader;
    }

    private LineMapper<OrderRecord> lineMapper() {
        DefaultLineMapper<OrderRecord> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "first_name", "last_name", "email", "supplier_pid", "credit_card_number",
                "credit_card_type", "order_id", "product_pid", "shipping_address", "country",
                "date_created", "quantity", "full_name", "order_status");

        BeanWrapperFieldSetMapper<OrderRecord> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(OrderRecord.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public OrderRecordProcessor processor(IntegrationService integrationService){
        return new OrderRecordProcessor(integrationService);
    }

    @Bean
    public OrderRecordWriter writer() {
        return new OrderRecordWriter();
    }

    @Bean
    public Step step() {
        return new StepBuilder("csvImport",jobRepository)
                .<OrderRecord, ProcessedOrder>chunk(batchSize,transactionManager)
                .reader(reader())
                .processor(processor(integrationService))
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job job() {
        return new JobBuilder("orderJob", jobRepository)
                .start(step())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(concurrencyLimit);
        return asyncTaskExecutor;
    }

}
