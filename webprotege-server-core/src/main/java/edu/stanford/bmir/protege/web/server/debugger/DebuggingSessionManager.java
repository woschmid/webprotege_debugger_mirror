package edu.stanford.bmir.protege.web.server.debugger;

import com.google.common.collect.ImmutableMap;
import edu.stanford.bmir.protege.web.server.debugger.diagnosis.DiagnosisEngineFactory;
import edu.stanford.bmir.protege.web.server.debugger.solver.SolverFactory;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingResult;
import edu.stanford.bmir.protege.web.shared.debugger.TestCase;
import edu.stanford.bmir.protege.web.shared.dispatch.ActionExecutionException;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
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
import java.util.*;

@ProjectSingleton
public class DebuggingSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(DebuggingSessionManager.class);

    private final RevisionManager revisionManager;

    private Map<ProjectId, DebuggingSession> debuggingSessions;

    @Inject
    public DebuggingSessionManager(RevisionManager revisionManager) {
        this.revisionManager = revisionManager;
        this.debuggingSessions = new HashMap<>();
    }

    public DebuggingResult startDebugging(ProjectId projectId) {
        final DebuggingSession session = getDebuggingSession(projectId);
        session.reset();
        return submitQuery(projectId);
    }

    public DebuggingResult stopDebugging(ProjectId projectId) {
        disposeDebuggingSession(projectId);
        return createDebuggingResult(null,null,null);
    }

    public DebuggingResult submitQuery(ProjectId projectId) {
        final DebuggingSession session = getDebuggingSession(projectId);
        return session.calc();
    }

    public void addAnswer(ProjectId projectId, ImmutableMap<String, Boolean> answers) {
        final DebuggingSession session = getDebuggingSession(projectId);
        session.addAnswer(answers);
    }

    private DebuggingSession getDebuggingSession(ProjectId projectId) {
        // TODO check which user is requesting for a debugging session
        synchronized (logger) {
            DebuggingSession debuggingSession = this.debuggingSessions.get(projectId);
            if (debuggingSession == null) {
                debuggingSession = createDebuggingSession(revisionManager);
                this.debuggingSessions.put(projectId, debuggingSession);
            }
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

    private void disposeDebuggingSession(ProjectId projectId) {
        final DebuggingSession session = getDebuggingSession(projectId);
        session.dispose();
        debuggingSessions.remove(projectId, session);
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
