package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingResult;
import edu.stanford.bmir.protege.web.shared.debugger.StartDebuggingAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class StartDebuggingActionHandler extends AbstractProjectActionHandler<StartDebuggingAction, DebuggingResult> {

    @Nonnull
    private final ProjectId projectId;

    @Nonnull
    private final RevisionManager revisionManager;

    @Nonnull
    private final DebuggingSessionManager debuggingSessionManager;


    @Inject
    public StartDebuggingActionHandler(@Nonnull ProjectId projectId,
                                       @Nonnull AccessManager accessManager,
                                       @Nonnull RevisionManager revisionManager,
                                       @Nonnull DebuggingSessionManager debuggingSessionManager
                                       ) {
        super(accessManager);
        this.projectId = checkNotNull(projectId);
        this.revisionManager = checkNotNull(revisionManager);
        this.debuggingSessionManager = checkNotNull(debuggingSessionManager);
    }

    @Nonnull
    @Override
    public Class<StartDebuggingAction> getActionClass() {
        return StartDebuggingAction.class;
    }

    @Nonnull
    @Override
    public DebuggingResult execute(@Nonnull StartDebuggingAction action, @Nonnull ExecutionContext executionContext) {
        return debuggingSessionManager.startDebugging(revisionManager);
    }
}
