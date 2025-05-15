package com.github.acs.file.batch.internal;

import com.github.acs.file.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class JobNotificationListenerTest {

    @Mock
    private JobExecution jobExecution;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private JobNotificationListener jobNotificationListener;

    @BeforeEach
    void setUp() {
        var jobInstance = mock(JobInstance.class);
        MockitoAnnotations.openMocks(this);

        when(jobExecution.getJobInstance()).thenReturn(jobInstance);
        when(jobInstance.getJobName()).thenReturn("Test Batch Job");


    }

    @Test
    void beforeJob() {
        jobNotificationListener.beforeJob(jobExecution);
    }

    @Test
    void afterJobCompleted() {
        var exitStatus = mock(ExitStatus.class);
        when(jobExecution.getExitStatus()).thenReturn(exitStatus);
        when(exitStatus.getExitCode()).thenReturn("COMPLETED");
        when(exitStatus.getExitDescription()).thenReturn("Job completed successfully");

        jobNotificationListener.afterJob(jobExecution);
    }

    @Test
    void afterJobError() {
        var exitStatus = mock(ExitStatus.class);
        when(jobExecution.getExitStatus()).thenReturn(exitStatus);
        when(exitStatus.getExitCode()).thenReturn("FAILED");
        when(exitStatus.getExitDescription()).thenReturn("Job failed");

        jobNotificationListener.afterJob(jobExecution);
    }
}