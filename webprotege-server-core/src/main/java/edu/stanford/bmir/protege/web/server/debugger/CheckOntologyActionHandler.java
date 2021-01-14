package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.debugger.CheckOntologyAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class CheckOntologyActionHandler extends AbstractProjectActionHandler<CheckOntologyAction, DebuggingSessionStateResult> {

    @Nonnull
    private final DebuggingSession session;

    @Inject
    public CheckOntologyActionHandler(@Nonnull DebuggingSession debuggingSession,
                                      @Nonnull AccessManager accessManager) {
        super(accessManager);
        this.session = checkNotNull(debuggingSession);
    }

    @Nonnull
    @Override
    public Class<CheckOntologyAction> getActionClass() {
        return CheckOntologyAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull CheckOntologyAction action, @Nonnull ExecutionContext executionContext) {
        try {
            return session.checkOntology(executionContext.getUserId());
        } catch (RuntimeException | OWLOntologyCreationException e) {
            session.stop();
            return DebuggingResultFactory.generateResult(session, Boolean.FALSE, e.getMessage());
        } catch (ConcurrentUserException | UnsatisfiedPreconditionException e) {
            return DebuggingResultFactory.generateResult(session, Boolean.FALSE, e.getMessage());
        }
    }

    @Nullable
    @Override
    protected BuiltInAction getRequiredExecutableBuiltInAction() {
        return BuiltInAction.DEBUG_ONTOLOGY;
    }
}