package com.example.batch.config;

import com.example.batch.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final static int CHUNK_SIZE = 10;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job testJob() {
        return jobBuilderFactory.get("testJob")
                .start(startStep())
                .build();
    }

    @Bean
    @JobScope
    public Step startStep() {
        return stepBuilderFactory.get("startStep")
                .<User, User>chunk(CHUNK_SIZE)
                .reader(reader(null))
                .processor(processor(null))
                .writer(writer(null))
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<User> reader(@Value("#{jobParameters[date]}") String date) {
        log.info("jobParameters value : " + date);
        Map<String, Object> params = new HashMap<>();
        params.put("amount", 10000);
        return new JpaPagingItemReaderBuilder<User>()
                .pageSize(CHUNK_SIZE)
                .parameterValues(params)
                .queryString("SELECT u FROM User u WHERE u.amount >= :amount ORDER BY id ASC")
                .entityManagerFactory(entityManagerFactory)
                .name("JpaPagingItemReader")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<User, User> processor(@Value("#{jobParameters[date]}") String date) {
        log.info("jobParameters value : " + date);
        return user -> {
            user.setAmount(user.getAmount() + 1000);
            return user;
        };
    }

    @Bean
    @StepScope
    public JpaItemWriter<User> writer(@Value("#{jobParameters[date]}") String date) {
        log.info("jobParameters value : " + date);
        return new JpaItemWriterBuilder<User>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

}
