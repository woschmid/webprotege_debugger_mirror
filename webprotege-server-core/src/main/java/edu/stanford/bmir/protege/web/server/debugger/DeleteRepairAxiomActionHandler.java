package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.DeleteRepairAxiomAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

@Deprecated
public class DeleteRepairAxiomActionHandler extends AbstractProjectActionHandler<DeleteRepairAxiomAction, DebuggingSessionStateResult> {

    @Nonnull
    private final DebuggingSession session;

    @Nonnull
    private final HasApplyChanges applyChanges;

    @Inject
    public DeleteRepairAxiomActionHandler(@Nonnull DebuggingSession session,
                                          @Nonnull AccessManager accessManager,
                                          @Nonnull HasApplyChanges applyChanges) {
        super(accessManager);
        this.session = session;
        this.applyChanges = applyChanges;
    }

    @Nonnull
    @Override
    public Class<DeleteRepairAxiomAction> getActionClass() {
        return DeleteRepairAxiomAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull DeleteRepairAxiomAction action, @Nonnull ExecutionContext executionContext) {
        try {
            return session.deleteRepairAxiom(executionContext.getUserId(), applyChanges, action.getAxiomToDelete());
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
