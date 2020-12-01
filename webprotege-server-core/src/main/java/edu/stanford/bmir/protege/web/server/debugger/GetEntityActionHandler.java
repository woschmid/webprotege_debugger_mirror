package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.debugger.GetEntityAction;
import edu.stanford.bmir.protege.web.shared.debugger.GetEntityResult;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class GetEntityActionHandler extends AbstractProjectActionHandler<GetEntityAction, GetEntityResult> {

    @Nonnull
    private final DebuggingSession session;

    @Inject
    public GetEntityActionHandler(@Nonnull DebuggingSession session,
                                  @Nonnull AccessManager accessManager) {
        super(accessManager);
        this.session = session;
    }

    @Nonnull
    @Override
    public Class<GetEntityAction> getActionClass() {
        return GetEntityAction.class;
    }

    @Nonnull
    @Override
    public GetEntityResult execute(@Nonnull GetEntityAction action, @Nonnull ExecutionContext executionContext) {
        return session.getEntity(executionContext.getUserId(), action.getAxiom());
    }

}
