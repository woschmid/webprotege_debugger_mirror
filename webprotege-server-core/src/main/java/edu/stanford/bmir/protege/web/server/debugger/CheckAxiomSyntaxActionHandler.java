package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.debugger.CheckAxiomSyntaxAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import org.semanticweb.owlapi.io.OWLParserException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Checks the correctness of a syntax of an owl axiom represented as string.
 */
public class CheckAxiomSyntaxActionHandler extends AbstractProjectActionHandler<CheckAxiomSyntaxAction, DebuggingSessionStateResult> {

    @Nonnull
    private final DebuggingSession session;

    @Inject
    public CheckAxiomSyntaxActionHandler(@Nonnull AccessManager accessManager, @Nonnull DebuggingSession session) {
        super(accessManager);
        this.session = checkNotNull(session);
    }

    @Nonnull
    @Override
    public Class<CheckAxiomSyntaxAction> getActionClass() {
        return CheckAxiomSyntaxAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull CheckAxiomSyntaxAction action, @Nonnull ExecutionContext executionContext) {
        try {
            return session.checkAxiomSyntax(executionContext.getUserId(), action.getAxiom());
        } catch (OWLParserException | ConcurrentUserException | UnsatisfiedPreconditionException e) {
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
