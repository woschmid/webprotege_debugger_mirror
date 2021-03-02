package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.SetFilterAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class SetFilterActionHandler extends AbstractProjectActionHandler<SetFilterAction, DebuggingSessionStateResult> {

    @Nonnull
    private final DebuggingSession session;

    @Inject
    public SetFilterActionHandler(@Nonnull AccessManager accessManager, @Nonnull DebuggingSession session) {
        super(accessManager);
        this.session = session;
    }

    @Nonnull
    @Override
    public Class<SetFilterAction> getActionClass() {
        return SetFilterAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull SetFilterAction action, @Nonnull ExecutionContext executionContext) {
        try {
            return session.setSearchFilter(executionContext.getUserId(), action.isABox(), action.isTBox(), action.isRBox());
        } catch (ConcurrentUserException e) {
            Util.logException(getActionClass(), e);
            return DebuggingResultFactory.generateResult(session, Boolean.FALSE, e.getMessage());
        }
    }

    @Nullable
    @Override
    protected BuiltInAction getRequiredExecutableBuiltInAction() {
        return BuiltInAction.DEBUG_ONTOLOGY;
    }
}
