package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.events.EventManager;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.RepairAction;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public class RepairActionHandler extends AbstractProjectActionHandler<RepairAction, DebuggingSessionStateResult> {

    @Nonnull
    private final DebuggingSession session;

    @Nonnull
    private final EventManager<ProjectEvent<?>> eventManager;

    @Nonnull
    private final HasApplyChanges applyChanges;

    public RepairActionHandler(@Nonnull DebuggingSession session,
                               @Nonnull AccessManager accessManager,
                               @Nonnull EventManager<ProjectEvent<?>> eventManager,
                               @Nonnull HasApplyChanges applyChanges) {
        super(accessManager);
        this.session = checkNotNull(session);
        this.eventManager = checkNotNull(eventManager);
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
            return session.repair(executionContext.getUserId(), eventManager, applyChanges);
        } catch (RuntimeException e) {
            session.stop();
            return DebuggingResultFactory.getFailureDebuggingSessionStateResult(session, e.getMessage());
        }
    }

}
