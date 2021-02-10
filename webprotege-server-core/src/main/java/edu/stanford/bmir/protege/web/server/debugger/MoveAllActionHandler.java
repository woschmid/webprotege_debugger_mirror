package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.MoveAllAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class MoveAllActionHandler extends AbstractProjectActionHandler<MoveAllAction, DebuggingSessionStateResult>  {

    @Nonnull
    private final DebuggingSession session;

    @Inject
    public MoveAllActionHandler(@Nonnull DebuggingSession session, @Nonnull AccessManager accessManager) {
        super(accessManager);
        this.session = checkNotNull(session);
    }

    @Nonnull
    @Override
    public Class<MoveAllAction> getActionClass() {
        return MoveAllAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull MoveAllAction action, @Nonnull ExecutionContext executionContext) {
        try {
            return session.moveAllAxiomsTo(executionContext.getUserId(), action.isMoveDown());
        } catch (UnsatisfiedPreconditionException | ConcurrentUserException | RuntimeException e) {
            return DebuggingResultFactory.generateResult(session, Boolean.FALSE, e.getMessage());
        }
    }

    @Nullable
    @Override
    protected BuiltInAction getRequiredExecutableBuiltInAction() {
        return BuiltInAction.DEBUG_ONTOLOGY;
    }
}
