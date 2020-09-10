package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.SubmitDebuggingAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class SubmitDebuggingActionHandler extends AbstractProjectActionHandler<SubmitDebuggingAction, DebuggingSessionStateResult> {

    @Nonnull
    private final ProjectId projectId;

    @Nonnull
    private final DebuggingSessionManager debuggingSessionManager;

    @Inject
    public SubmitDebuggingActionHandler(@Nonnull ProjectId projectId,
                                        @Nonnull AccessManager accessManager,
                                        @Nonnull DebuggingSessionManager debuggingSessionManager) {
        super(accessManager);
        this.projectId = checkNotNull(projectId);
        this.debuggingSessionManager = checkNotNull(debuggingSessionManager);
    }

    @Nonnull
    @Override
    public Class<SubmitDebuggingAction> getActionClass() {
        return SubmitDebuggingAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull SubmitDebuggingAction action, @Nonnull ExecutionContext executionContext) {
        return debuggingSessionManager.submitQuery(this.projectId, executionContext.getUserId(), action.getAnswers());
    }

    @Nullable
    @Override
    protected BuiltInAction getRequiredExecutableBuiltInAction() {
        return BuiltInAction.DEBUG_ONTOLOGY;
    }
}
