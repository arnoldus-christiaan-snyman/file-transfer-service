package com.github.acs.file.batch;

public interface BatchTrigger {

    void triggerBatch() throws BatchProcessException;

}
