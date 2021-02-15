package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.PaginationAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class PaginationActionHandler extends AbstractProjectActionHandler<PaginationAction, DebuggingSessionStateResult>  {

    @Nonnull
    private final DebuggingSession session;

    @Inject
    public PaginationActionHandler(@Nonnull AccessManager accessManager, @Nonnull DebuggingSession session) {
        super(accessManager);
        this.session = session;
    }

    @Nonnull
    @Override
    public Class<PaginationAction> getActionClass() {
        return PaginationAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull PaginationAction action, @Nonnull ExecutionContext executionContext) {
        try {
            return session.paginate(executionContext.getUserId(), action.getPageFlag(), action.getPage());
        } catch (ConcurrentUserException | RuntimeException  e) {
            return DebuggingResultFactory.generateResult(session, Boolean.FALSE, e.getMessage());
        }
    }

    @Nullable
    @Override
    protected BuiltInAction getRequiredExecutableBuiltInAction() {
        return BuiltInAction.DEBUG_ONTOLOGY;
    }

}
