package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.debugger.diagnosis.DiagnosisEngineFactory;
import edu.stanford.bmir.protege.web.server.debugger.solver.SolverFactory;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingResult;
import edu.stanford.bmir.protege.web.shared.debugger.TestCase;
import edu.stanford.bmir.protege.web.shared.dispatch.ActionExecutionException;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Query;
import org.exquisite.core.solver.ISolver;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

@ProjectSingleton
public class DebuggingSessionManagerImpl implements DebuggingSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(DebuggingSessionManagerImpl.class);

    private DebuggingSession debuggingSession;

    @Inject
    public DebuggingSessionManagerImpl() {
    }

    @Override
    public DebuggingResult startDebugging(@Nonnull RevisionManager revisionManager) {
        final DebuggingSession session = getDebuggingSession(revisionManager);
        session.reset();
        return submitQuery();
    }

    @Override
    public DebuggingResult stopDebugging() {
        debuggingSession.dispose();
        debuggingSession = null;
        return createDebuggingResult(null,null,null);
    }

    @Override
    public DebuggingResult submitQuery() {
        return debuggingSession.calc();
    }

    private DebuggingSession getDebuggingSession(@Nonnull RevisionManager revisionManager) {
        // create a singleton instance of a debugging session
        // TODO check which user is requesting for a debugging session
        synchronized (logger) {
            if (debuggingSession == null)
                debuggingSession = createDebuggingSession(revisionManager);
            return debuggingSession;
        }
    }

    /**
     * Starts a debugging session.
     * Prepares everything that is necessary for a debugging session and returns a DebuggingSession if sucessfully.
     * @return DebuggingSession if successfully prepare and start a debugging session.
     */
    private DebuggingSession createDebuggingSession(@Nonnull RevisionManager revisionManager) {
        final OWLOntologyManager ontologyManager = revisionManager.getOntologyManagerForRevision(revisionManager.getCurrentRevision());
        final List<OWLOntology> ontologies = new ArrayList<>(ontologyManager.getOntologies());
        if (ontologies.size() != 1)
            throw new ActionExecutionException(new RuntimeException("Expected only one ontology but there are " + ontologies.size()));

        final OWLOntology ontology = ontologies.get(0);
        logger.info("Got ontology {})", ontology);

        // creates a solver instance  using the ontology
        final ISolver<OWLLogicalAxiom> solver = SolverFactory.getSolver(ontology);
        logger.info("solver created {}", solver);

        // create diagnosis engine using the solver
        IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = DiagnosisEngineFactory.getDiagnosisEngine(solver);
        logger.info("diagnosis engine created: {}", diagnosisEngine);

        return new DebuggingSession(diagnosisEngine);

    }

    @Nonnull
    protected static DebuggingResult createDebuggingResult(@Nullable Query<OWLLogicalAxiom> query, @Nullable Set<Diagnosis<OWLLogicalAxiom>> diagnoses, @Nullable DiagnosisModel<OWLLogicalAxiom> diagnosisModel) {

        edu.stanford.bmir.protege.web.shared.debugger.Query q = null;
        List<edu.stanford.bmir.protege.web.shared.debugger.Diagnosis> d = new ArrayList<>();
        List<TestCase> p = new ArrayList<>();
        List<TestCase> n = new ArrayList<>();

        if (query != null)
            q = new edu.stanford.bmir.protege.web.shared.debugger.Query(query.formulas);

        if (diagnoses != null) {
            for (org.exquisite.core.model.Diagnosis<OWLLogicalAxiom> diag : diagnoses)
                d.add(new edu.stanford.bmir.protege.web.shared.debugger.Diagnosis(diag.getFormulas()));
        }

        if (diagnosisModel != null)
            for (OWLLogicalAxiom a : diagnosisModel.getEntailedExamples())
                p.add(new TestCase(a));


        if (diagnosisModel != null)
            for (OWLLogicalAxiom a : diagnosisModel.getNotEntailedExamples())
                n.add(new TestCase(a));

        return new DebuggingResult(q, d, p, n);
    }
}
