package edu.stanford.bmir.protege.web.server.debugger;

import com.google.common.collect.ImmutableMap;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggerStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingResult;
import edu.stanford.bmir.protege.web.shared.debugger.SessionState;
import edu.stanford.bmir.protege.web.shared.dispatch.ActionExecutionException;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.user.UserId;
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
import java.util.*;


@ProjectSingleton
public class DebuggingSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(DebuggingSessionManager.class);

    private final RevisionManager revisionManager;

    private final Map<ProjectId, Map<UserId, DebuggingSession>> debuggingSessions;

    @Inject
    public DebuggingSessionManager(RevisionManager revisionManager) {
        this.revisionManager = revisionManager;
        this.debuggingSessions = new HashMap<>();
    }

    /**
     * Starts a new debugging session for the project.
     *
     * @param projectId The project.
     * @param userId
     * @return A result representing the current state of the debugging session.
     */
    public DebuggingResult startDebugging(ProjectId projectId, UserId userId) {
        final DebuggingSession session = getDebuggingSession(projectId, userId);
        session.start();
        return session.calculateQuery(null);
    }

    /**
     * Submits a query and it's answers to the debugger.
     *
     * @param projectId The project.
     * @param userId
     * @param answers Map containing answers to the previously given query.
     * @return A result representing the current state of the debugging session.
     */
    public DebuggingResult submitQuery(@Nonnull ProjectId projectId, UserId userId, @Nullable ImmutableMap<String, Boolean> answers) {
        final DebuggingSession session = getDebuggingSession(projectId, userId);
        return session.calculateQuery(answers);
    }

    /**
     * Stops the runnning debugging session of the project.
     *
     * @param projectId The project.
     * @param userId
     * @return A result representing the current state of the debugging session.
     */
    public DebuggingResult stopDebugging(ProjectId projectId, UserId userId) {
        final DebuggingSession session = getDebuggingSession(projectId, userId);
        session.stop();
        final boolean isRemoved = debuggingSessions.remove(projectId, Collections.singletonMap(userId,session));
        if (!isRemoved)
            throw new ActionExecutionException(new RuntimeException("The debugging session could not be stopped appropriately"));
        return DebuggingResultFactory.getDebuggingResult(null,null,null, session.getState());
    }

    /**
     * Stops the runnning debugging session of the project.
     *
     * @param projectId The project.
     * @param userId
     * @return A result representing the current state of the debugging session.
     */
    public DebuggerStateResult getDebuggingState(ProjectId projectId, UserId userId) {
//        logger.info("getDebuggingState created for project {}", projectId);
        final DebuggingSession session = getDebuggingSession(projectId, userId);
        return new DebuggerStateResult(SessionState.INIT);
    }

    /**
     * Returns a debugging session belonging exclusively to the project.
     *
     * @param projectId A project.
     * @param userId
     * @return A debugging session instance.
     */
    private DebuggingSession getDebuggingSession(ProjectId projectId, UserId userId) {
        synchronized (logger) {
            DebuggingSession debuggingSession;
            Map<UserId, DebuggingSession> debuggingSessionOfProject = this.debuggingSessions.get(projectId);
            if (debuggingSessionOfProject == null) {
                // project yet has no debugging session
                debuggingSession = createDebuggingSession(revisionManager);
                debuggingSessionOfProject = Collections.singletonMap(userId, debuggingSession); // only a single debugging session is allowed per project
                this.debuggingSessions.put(projectId, debuggingSessionOfProject);
                logger.info("DebuggingSession {} created for user {} in project {}", debuggingSessionOfProject, userId, projectId);
            } else {
                // there exists a debugging session for this project
                debuggingSession = debuggingSessionOfProject.get(userId);
                // check if the user is it's owner
                if (debuggingSession == null)
                    throw new ActionExecutionException(new RuntimeException("A debugging session is already running for this project by user " + userId + "!"));
            }
            return debuggingSession;
        }
    }

    /**
     * Creates a new instance of a debugging session.
     *
     * Prepares everything that is necessary for a debugging session and returns a DebuggingSession if sucessfully.
     * @return DebuggingSession if successfully prepare and start a debugging session.
     */
    private DebuggingSession createDebuggingSession(@Nonnull RevisionManager revisionManager) {
        final OWLOntologyManager ontologyManager = revisionManager.getOntologyManagerForRevision(revisionManager.getCurrentRevision());
        final List<OWLOntology> ontologies = new ArrayList<>(ontologyManager.getOntologies());
        if (ontologies.size() != 1)
            throw new ActionExecutionException(new RuntimeException("Expected only one ontology but there are " + ontologies.size()));

        final OWLOntology ontology = ontologies.get(0);
        logger.info("Found ontology {} from current revision {})", ontology, revisionManager.getCurrentRevision());

        // creates a solver instance  using the ontology
        final ISolver<OWLLogicalAxiom> solver = SolverFactory.getSolver(ontology);
        logger.info("Solver created: {}", solver);

        // create diagnosis engine using the solver
        IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = DiagnosisEngineFactory.getDiagnosisEngine(solver);
        logger.info("Diagnosis engine created: {}", diagnosisEngine);

        return new DebuggingSession(diagnosisEngine);
    }

}
