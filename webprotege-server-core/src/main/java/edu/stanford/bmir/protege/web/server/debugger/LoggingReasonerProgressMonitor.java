package edu.stanford.bmir.protege.web.server.debugger;

import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default reasoner progress monitor.
 *
 * Some reasoners such as Pellet require to have one!
 * This is an adapted copy of TimedConsoleProgressMonitor where logger is used instead of System.out.
 */
public class LoggingReasonerProgressMonitor implements ReasonerProgressMonitor {

    private static final long serialVersionUID = 2342324234324234L;

    private static final Logger logger = LoggerFactory.getLogger(LoggingReasonerProgressMonitor.class);

    // private long lastTime; // last time a task has started in nanoseconds

    private long beginTime; // time in nanoseconds

    private String taskName;

    @Override
    public void reasonerTaskStarted(String taskName) {
        logger.info("{} ...", taskName);
        this.beginTime = System.nanoTime();
        this.taskName = taskName;
    }

    @Override
    public void reasonerTaskStopped() {
        logger.info("{} finished in {} ms.", taskName, (double)(System.nanoTime() - this.beginTime) / 1000000.0D);
    }

    @Override
    public void reasonerTaskProgressChanged(int value, int max) {
        /*
        final long time = System.nanoTime();
        if (max > 0) {
            final int percent = value * 100 / max;
            logger.info("    {}", ("" + percent + "%\t" + (time - this.lastTime) / 1000000L) + "ms");
            this.lastTime = time;
        }
        */
    }

    @Override
    public void reasonerTaskBusy() {
        logger.info("{} busy ...", taskName);
    }
}
