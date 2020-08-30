package edu.stanford.bmir.protege.web.server.debugger;

import org.exquisite.core.IExquisiteProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingQueryProgressMonitor implements IExquisiteProgressMonitor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingQueryProgressMonitor.class);

    private String taskName;

    private long beginTime; // time in nanoseconds

    @Override
    public void taskStarted(String taskName) {
        logger.info("{}", taskName);
        this.taskName = taskName;
        this.beginTime = System.nanoTime();
    }

    @Override
    public void taskBusy(String message) {
        logger.info("\t{}", message);
    }

    @Override
    public void taskProgressChanged(String message, int value, int max) {
        logger.info("\t{} {} {}", message, value, max);
    }

    @Override
    public void taskStopped() {
        logger.info("\t... query generation finished.");
        logger.info("{} finished after {} ms.", taskName, (double)(System.nanoTime() - this.beginTime) / 1000000.0D);
    }

    @Override
    public void setCancel(boolean b) {

    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setVerbose(boolean b) {

    }

    @Override
    public boolean isVerbose() {
        return false;
    }
}
