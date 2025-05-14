package com.github.acs.file.batch.internal;

import com.github.acs.file.email.EmailRequest;
import com.github.acs.file.email.EmailService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class JobNotificationListener implements JobExecutionListener {

    private final MeterRegistry registry;
    private final EmailService emailService;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Batch process started with job name: {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Batch process completed successfully with job name: {}", jobExecution.getJobInstance().getJobName());

        } else if(jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Batch process failed with job name: {}", jobExecution.getJobInstance().getJobName());
        }
    }

    private void sendCompletedEmail() {
        //
    }

    private void sendFailedEmail() {
        //
    }
}
