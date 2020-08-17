package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.debugger.StartDebuggingAction;
import edu.stanford.bmir.protege.web.shared.debugger.StartDebuggingResult;
import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.engines.HSTreeEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.exquisite.core.solver.ISolver;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Set;

public class StartDebuggingActionHandler extends AbstractProjectActionHandler<StartDebuggingAction, StartDebuggingResult> {

    @Inject
    public StartDebuggingActionHandler(@Nonnull AccessManager accessManager) {
        super(accessManager);
    }

    @Nonnull
    @Override
    public Class<StartDebuggingAction> getActionClass() {
        return StartDebuggingAction.class;
    }

    @Nonnull
    @Override
    public StartDebuggingResult execute(@Nonnull StartDebuggingAction action, @Nonnull ExecutionContext executionContext) {
        try {
            // HermiT reasoner availability test
            org.semanticweb.HermiT.ReasonerFactory reasonerFactory = new org.semanticweb.HermiT.ReasonerFactory();

            // diagnosis module availabilty test
            ISolver<Integer> solver = new ISolver<Integer>() {
                @Override
                public boolean isConsistent(Collection<Integer> collection) {
                    return false;
                }

                @Override
                public boolean isEntailed(Collection<Integer> collection, Collection<Integer> collection1) {
                    return false;
                }

                @Override
                public Set<Integer> calculateEntailments(Collection<Integer> collection) {
                    return null;
                }

                @Override
                public DiagnosisModel<Integer> getDiagnosisModel() {
                    return null;
                }

                @Override
                public void dispose() {

                }
            };
            IDiagnosisEngine diagnosisEngine = getDiagnosisEngine(solver);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new StartDebuggingResult("Hi, you pressed the Start button!");
        }
    }

    protected IDiagnosisEngine getDiagnosisEngine(ISolver solver) {
        return new HSTreeEngine<>(solver, new QuickXPlain(solver));
    }
}
