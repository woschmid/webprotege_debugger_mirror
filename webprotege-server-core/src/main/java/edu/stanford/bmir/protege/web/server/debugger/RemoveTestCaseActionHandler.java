package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.RemoveTestCaseAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class RemoveTestCaseActionHandler extends AbstractProjectActionHandler<RemoveTestCaseAction, DebuggingSessionStateResult> {

    @Nonnull
    private final DebuggingSession session;

    @Inject
    public RemoveTestCaseActionHandler(@Nonnull AccessManager accessManager, @Nonnull DebuggingSession session) {
        super(accessManager);
        this.session = session;
    }

    @Nonnull
    @Override
    public Class<RemoveTestCaseAction> getActionClass() {
        return RemoveTestCaseAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull RemoveTestCaseAction action, @Nonnull ExecutionContext executionContext) {
        try {
            return session.removeTestCase(executionContext.getUserId(), action.getTestCase());
        } catch (RuntimeException e) {
            // session.stop();
            return DebuggingResultFactory.generateResult(session, Boolean.FALSE, e.getMessage());
        }
    }

    @Nullable
    @Override
    protected BuiltInAction getRequiredExecutableBuiltInAction() {
        return BuiltInAction.DEBUG_ONTOLOGY;
    }
}
