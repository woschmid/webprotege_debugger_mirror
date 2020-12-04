package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.AddTestCaseAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class AddTestCaseActionHandler extends AbstractProjectActionHandler<AddTestCaseAction, DebuggingSessionStateResult>  {

    @Nonnull
    private final DebuggingSession session;

    @Inject
    public AddTestCaseActionHandler(@Nonnull AccessManager accessManager, @Nonnull DebuggingSession session) {
        super(accessManager);
        this.session = session;
    }

    @Nonnull
    @Override
    public Class<AddTestCaseAction> getActionClass() {
        return AddTestCaseAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull AddTestCaseAction action, @Nonnull ExecutionContext executionContext) {
        try {
            return session.addTestCase(executionContext.getUserId(), action.getTestCase(), action.isEntailed());
        } catch (RuntimeException e) {
            session.stop();
            return DebuggingResultFactory.generateResult(session, Boolean.FALSE, e.getMessage());
        }
    }

    @Nullable
    @Override
    protected BuiltInAction getRequiredExecutableBuiltInAction() {
        return BuiltInAction.DEBUG_ONTOLOGY;
    }

}
