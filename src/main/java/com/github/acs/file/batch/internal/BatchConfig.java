package com.github.acs.file.batch.internal;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public final class BatchConfig {

    @Bean
    public Job fileTransferBatchJob(JobRepository jobRepository, Step step, JobNotificationListener listener) {
        return new JobBuilder("fileTransferBatchJob", jobRepository)
                .listner()
                .start(step)
                .next(step)
                .build();
    }

}
