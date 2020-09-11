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

    private long beginTime; // time in nanoseconds

    private String taskName;

    private DebuggingSession debuggingSession;

    public LoggingReasonerProgressMonitor(DebuggingSession session) {
        this.debuggingSession = session;
    }

    @Override
    public void reasonerTaskStarted(String taskName) {
        logger.info("{} Reasoner: {} ...", debuggingSession, taskName);
        this.beginTime = System.nanoTime();
        this.taskName = taskName;
    }

    @Override
    public void reasonerTaskStopped() {
        logger.info("{} Reasoner: {} finished in {} ms.", debuggingSession, taskName, ((double)(System.nanoTime() - this.beginTime) / 1000000.0D));
    }

    @Override
    public void reasonerTaskProgressChanged(int value, int max) {
    }

    @Override
    public void reasonerTaskBusy() {
        logger.info("{} Reasoner: {} busy ...", debuggingSession, taskName);
    }
}
