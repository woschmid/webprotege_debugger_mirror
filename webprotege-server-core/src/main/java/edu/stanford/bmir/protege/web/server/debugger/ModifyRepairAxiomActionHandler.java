package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.ModifyRepairAxiomAction;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class ModifyRepairAxiomActionHandler extends AbstractProjectActionHandler<ModifyRepairAxiomAction, DebuggingSessionStateResult> {

    @Nonnull
    private final DebuggingSession session;

    @Nonnull
    private final HasApplyChanges applyChanges;

    @Inject
    public ModifyRepairAxiomActionHandler(@Nonnull DebuggingSession session,
                                          @Nonnull AccessManager accessManager,
                                          @Nonnull HasApplyChanges applyChanges) {
        super(accessManager);
        this.session = session;
        this.applyChanges = applyChanges;
    }

    @Nonnull
    @Override
    public Class<ModifyRepairAxiomAction> getActionClass() {
        return ModifyRepairAxiomAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull ModifyRepairAxiomAction action, @Nonnull ExecutionContext executionContext) {
        try {
            return session.modifyRepairAxiom(executionContext.getUserId(), applyChanges, action.getOriginalAxiom(), action.getModifiedAxiom());
        } catch (RuntimeException e) {
            session.stop();
            return DebuggingResultFactory.generateResult(session, Boolean.FALSE, e.getMessage());
        }
    }

}
