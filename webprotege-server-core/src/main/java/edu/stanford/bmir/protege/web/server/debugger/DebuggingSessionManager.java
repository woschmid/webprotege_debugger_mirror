package edu.stanford.bmir.protege.web.server.debugger;

import com.google.common.collect.ImmutableMap;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingResult;
import edu.stanford.bmir.protege.web.shared.dispatch.ActionExecutionException;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.exquisite.core.engines.IDiagnosisEngine;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        session.start();
        return session.calc(null);
    }

    public DebuggingResult submitQuery(@Nonnull ProjectId projectId, @Nullable ImmutableMap<String, Boolean> answers) {
        final DebuggingSession session = getDebuggingSession(projectId);
        return session.calc(answers);
    }

    public DebuggingResult stopDebugging(ProjectId projectId) {
        final DebuggingSession session = getDebuggingSession(projectId);
        session.stop();
        debuggingSessions.remove(projectId, session);
        return DebuggingResultFactory.getDebuggingResult(null,null,null);
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

}
