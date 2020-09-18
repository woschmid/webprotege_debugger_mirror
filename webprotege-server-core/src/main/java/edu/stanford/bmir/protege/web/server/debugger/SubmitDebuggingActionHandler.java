package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.SubmitDebuggingAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class SubmitDebuggingActionHandler extends AbstractProjectActionHandler<SubmitDebuggingAction, DebuggingSessionStateResult> {

    @Nonnull
    private final DebuggingSession session;

    @Inject
    public SubmitDebuggingActionHandler(@Nonnull DebuggingSession debuggingSession,
                                        @Nonnull AccessManager accessManager) {
        super(accessManager);
        this.session = checkNotNull(debuggingSession);
    }

    @Nonnull
    @Override
    public Class<SubmitDebuggingAction> getActionClass() {
        return SubmitDebuggingAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull SubmitDebuggingAction action, @Nonnull ExecutionContext executionContext) {
        return session.calculateQuery(executionContext.getUserId(), action.getAnswers());
    }

    @Nullable
    @Override
    protected BuiltInAction getRequiredExecutableBuiltInAction() {
        return BuiltInAction.DEBUG_ONTOLOGY;
    }
}
