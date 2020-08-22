package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.debugger.diagnosis.DiagnosisEngineFactory;
import edu.stanford.bmir.protege.web.server.debugger.solver.SolverFactory;
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
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@ProjectSingleton
public class DebuggingSessionManagerImpl implements DebuggingSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(DebuggingSessionManagerImpl.class);

    @Nonnull
    private final RevisionManager revisionManager;

    @Nonnull
    private final ProjectId projectId;

    private DebuggingSession debuggingSession;

    @Inject
    public DebuggingSessionManagerImpl(@Nonnull RevisionManager revisionManager,
                                       @Nonnull ProjectId projectId) {
        this.revisionManager = checkNotNull(revisionManager);
        this.projectId = checkNotNull(projectId);
    }

    @Override
    public DebuggingResult startDebugging() {
        if (debuggingSession == null)
            debuggingSession = createDebuggingSession();
        debuggingSession.reset();
        return debuggingSession.calc();
    }

    @Override
    public DebuggingResult stopDebugging() {
        DebuggingResult result = debuggingSession.dispose();
        debuggingSession = null;
        return result;
    }

    @Override
    public DebuggingResult submitQuery() {
        return debuggingSession.calc();
    }

    /**
     * Starts a debugging session.
     * Prepares everything that is necessary for a debugging session and returns a DebuggingSession if sucessfully.
     * @return DebuggingSession if successfully prepare and start a debugging session.
     */
    private DebuggingSession createDebuggingSession() {
        final OWLOntologyManager ontologyManager = this.revisionManager.getOntologyManagerForRevision(this.revisionManager.getCurrentRevision());
        final List<OWLOntology> ontologies = new ArrayList<>(ontologyManager.getOntologies());
        if (ontologies.size() != 1)
            throw new ActionExecutionException(new RuntimeException("Expected only one ontology for project " + projectId + " but there are " + ontologies.size()));

        final OWLOntology ontology = ontologies.get(0);
        logger.info("{} has ontology {})", projectId, ontology);

        // creates a solver instance  using the ontology
        final ISolver<OWLLogicalAxiom> solver = SolverFactory.getSolver(ontology);
        logger.info("{} solver created {}", projectId, solver);

        // create diagnosis engine using the solver
        IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = DiagnosisEngineFactory.getDiagnosisEngine(solver);
        logger.info("{} diagnosis engine created: {}", projectId, diagnosisEngine);

        return new DebuggingSession(projectId, diagnosisEngine);

    }

}
