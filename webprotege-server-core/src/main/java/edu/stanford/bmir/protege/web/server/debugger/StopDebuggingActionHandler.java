package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.StopDebuggingAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class StopDebuggingActionHandler extends AbstractProjectActionHandler<StopDebuggingAction, DebuggingSessionStateResult> {

    @Nonnull
    private final ProjectId projectId;

    @Nonnull
    private final DebuggingSessionManager debuggingSessionManager;

    @Inject
    public StopDebuggingActionHandler(@Nonnull ProjectId projectId,
                                      @Nonnull AccessManager accessManager,
                                      @Nonnull DebuggingSessionManager debuggingSessionManager) {
        super(accessManager);
        this.projectId = checkNotNull(projectId);
        this.debuggingSessionManager = checkNotNull(debuggingSessionManager);
    }

    @Nonnull
    @Override
    public Class<StopDebuggingAction> getActionClass() {
        return StopDebuggingAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull StopDebuggingAction action, @Nonnull ExecutionContext executionContext) {
        return this.debuggingSessionManager.stopDebugging(projectId, executionContext.getUserId());
    }

    @Nullable
    @Override
    protected BuiltInAction getRequiredExecutableBuiltInAction() {
        return BuiltInAction.DEBUG_ONTOLOGY;
    }
}
