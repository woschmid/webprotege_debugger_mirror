package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.RepairAction;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class RepairActionHandler extends AbstractProjectActionHandler<RepairAction, DebuggingSessionStateResult> {

    @Nonnull
    private final DebuggingSession session;

    @Nonnull
    private final HasApplyChanges applyChanges;

    @Inject
    public RepairActionHandler(@Nonnull DebuggingSession session,
                               @Nonnull AccessManager accessManager,
                               @Nonnull HasApplyChanges applyChanges) {
        super(accessManager);
        this.session = checkNotNull(session);
        this.applyChanges = checkNotNull(applyChanges);
    }

    @Nonnull
    @Override
    public Class<RepairAction> getActionClass() {
        return RepairAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull RepairAction action, @Nonnull ExecutionContext executionContext) {
        try {
            return session.repair(executionContext.getUserId(), applyChanges);
        } catch (RuntimeException e) {
            session.stop();
            return DebuggingResultFactory.generateResult(session, Boolean.FALSE, e.getMessage());
        }
    }

}
