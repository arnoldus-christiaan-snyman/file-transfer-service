package com.github.acs.file.batch.internal;

import com.github.acs.file.batch.internal.template.CompletedEmailTemplate;
import com.github.acs.file.batch.internal.template.CompletedTemplateVariables;
import com.github.acs.file.batch.internal.template.ErrorEmailTemplate;
import com.github.acs.file.batch.internal.template.ErrorTemplateVariables;
import com.github.acs.file.email.EmailRequest;
import com.github.acs.file.email.EmailService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

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
        if(jobExecution.getExitStatus().getExitCode().equals(ExitStatus.COMPLETED.getExitCode())) {
            this.sendCompletedEmail(jobExecution);
            log.info("Batch process completed successfully with job name: {}", jobExecution.getJobInstance().getJobName());

        } else if(jobExecution.getExitStatus().getExitCode().equals(ExitStatus.FAILED.getExitCode())) {
            this.sendFailedEmail(jobExecution);
            log.error("Batch process failed with job name: {}", jobExecution.getJobInstance().getJobName());
        }
    }

    private void sendCompletedEmail(JobExecution jobExecution) {
        var templateVariables = CompletedTemplateVariables.builder()
                .timeOfDay(LocalDateTime.now().toString())
                .jobName(jobExecution.getJobInstance().getJobName())
                .completedTime(jobExecution.getEndTime())
                .passedCount(0)
                .build();

        var emailTemplate = CompletedEmailTemplate.builder()
                .completedTemplateVariables(templateVariables)
                .build();

        var emailRequest = EmailRequest.builder()
                .to(Set.of("test@acs.com"))
                .subject("Batch process completed")
                .template(emailTemplate)
                .build();

        this.emailService.sendEmail(emailRequest);
    }

    private void sendFailedEmail(JobExecution jobExecution) {
        var templateVariables = ErrorTemplateVariables.builder()
                .timeOfDay(LocalDateTime.now())
                .failureTime(jobExecution.getEndTime())
                .jobName(jobExecution.getJobInstance().getJobName())
                //TODO add the error message properly
                .errorMessage(jobExecution.getFailureExceptions().toString())
                .build();

        var emailTemplate = ErrorEmailTemplate.builder()
                .errorTemplateVariables(templateVariables)
                .build();

        var emailRequest = EmailRequest.builder()
                .to(Set.of("test@acs.com"))
                .subject("Batch process failed")
                .template(emailTemplate)
                .build();

        this.emailService.sendEmail(emailRequest);
    }
}
