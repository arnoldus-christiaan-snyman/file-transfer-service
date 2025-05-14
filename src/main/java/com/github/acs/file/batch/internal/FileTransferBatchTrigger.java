package com.github.acs.file.batch.internal;

import com.github.acs.file.batch.BatchProcessException;
import com.github.acs.file.batch.BatchTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.Arrays;

@Slf4j
public final class FileTransferBatchTrigger implements BatchTrigger {

    private final JobLauncher jobLauncher;
    private final Job batchJob;

    public FileTransferBatchTrigger(JobLauncher jobLauncher, Job batchJob) {
        this.jobLauncher = jobLauncher;
        this.batchJob = batchJob;
    }

    @Override
    public void triggerBatch() throws BatchProcessException {
        JobParameters parameters = getParameters(); // Get job parameters
        try{
            this.jobLauncher.run(this.batchJob, parameters);  // Launch the job
        } catch (JobInstanceAlreadyCompleteException e) {
            log.warn("Job instance already complete", e);
            throw new BatchProcessException(e);
        } catch (JobExecutionAlreadyRunningException e) {
            log.warn("Job execution already running", e);
            throw new BatchProcessException(e);
        } catch (JobParametersInvalidException e) {
            log.error("Invalid job parameters: {}", Arrays.toString(parameters.getParameters().entrySet().toArray()), e);
            throw new BatchProcessException(e);
        } catch (JobRestartException e) {
            log.error("Job restart failed", e);
            throw new BatchProcessException(e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while triggering batch job", e);
            throw new BatchProcessException(e);
        } finally {
            log.info("Batch job completed with parameters: {}", Arrays.toString(parameters.getParameters().entrySet().toArray()));
        }
    }

    private JobParameters getParameters() {
        return new JobParametersBuilder()
                .addString("fileName", "example.txt") // Add your parameters here
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
    }

}
