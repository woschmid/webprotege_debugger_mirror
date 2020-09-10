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
import java.util.concurrent.ConcurrentHashMap;


@ProjectSingleton
public class DebuggingSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(DebuggingSessionManager.class);

    private final RevisionManager revisionManager;

    private final Map<ProjectId, DebuggingSession> debuggingSessions;

    @Inject
    public DebuggingSessionManager(@Nonnull RevisionManager revisionManager) {
        this.revisionManager = revisionManager;
        this.debuggingSessions = new ConcurrentHashMap<>();
    }

    /**
     * Starts a new debugging session for the project.
     *
     * @param projectId The project.
     * @param userId The user who wants to start a debugging session.
     * @return A result representing the current state of the debugging session.
     */
    @Nonnull public DebuggingResult startDebugging(@Nonnull ProjectId projectId, @Nonnull UserId userId) {
        final DebuggingSession session = getAuthorizedDebuggingSession(projectId, userId);
        session.start();
        return session.calculateQuery(null);
    }

    /**
     * Submits a query and it's answers to the debugger.
     *
     * @param projectId The project.
     * @param userId The user who wants to submit a query.
     * @param answers Map containing answers to the previously given query.
     * @return A result representing the current state of the debugging session.
     */
    @Nonnull public DebuggingResult submitQuery(@Nonnull ProjectId projectId, @Nonnull UserId userId, @Nullable ImmutableMap<String, Boolean> answers) {
        final DebuggingSession session = getAuthorizedDebuggingSession(projectId, userId);
        return session.calculateQuery(answers);
    }

    /**
     * Stops the running debugging session of the project.
     *
     * @param projectId The project.
     * @param userId The user who wants to stop a debugging session.
     * @return A result representing the current state of the debugging session.
     */
    @Nonnull public DebuggingResult stopDebugging(@Nonnull ProjectId projectId, @Nonnull UserId userId) {
        final DebuggingSession session = getAuthorizedDebuggingSession(projectId, userId);
        session.stop();
        final boolean isRemoved = debuggingSessions.remove(projectId, session);
        if (!isRemoved)
            throw new ActionExecutionException(new RuntimeException("The debugging session could not be stopped appropriately"));
        return DebuggingResultFactory.getDebuggingResult(session);
    }

    /**
     * Stops the running debugging session of the project.
     *
     * @param projectId The project.
     * @param userId The user who wants to get the state of the debugging session.
     * @return A result representing the current state of the debugging session.
     */
    public DebuggerStateResult getDebuggingState(@Nonnull ProjectId projectId, @Nonnull UserId userId) {
        final DebuggingSession session = getDebuggingSession(projectId);
        return new DebuggerStateResult(SessionState.INIT);
    }

    /**
     * Returns a debugging session belonging exclusively to this project and this user.<br/>
     * If there does not yet exist a session for this project a new debugging session will be created belonging
     * to this user.<br/>
     * If there exists a debugging session for this project which this user is NOT owner of an exception will be
     * thrown.
     *
     * <strong>Use this method only if you must manipulate the debugging session</strong>.
     *
     * @param projectId A project.
     * @param userId The user who requests authorized access to his/her debugging session of this project.
     * @return A debugging session instance.
     *
     */
    @Nonnull private DebuggingSession getAuthorizedDebuggingSession(@Nonnull ProjectId projectId, @Nonnull UserId userId) {
        DebuggingSession debuggingSession = this.debuggingSessions.get(projectId);
        // only a single debugging session is allowed per project
        if (debuggingSession == null) { // the project yet has no debugging session
            debuggingSession = createDebuggingSession(projectId, userId);
            this.debuggingSessions.put(projectId, debuggingSession);
            logger.info("{} created for {} in {}", debuggingSession, userId, projectId);
        } else { // there exists a debugging session for this project
            // check if the user is it's owner
            if (!debuggingSession.getUserId().equals(userId))
                throw new ActionExecutionException(new RuntimeException("A debugging session is already running for this project by user " + debuggingSession.getUserId() + "!"));
        }
        return debuggingSession;
    }

    /**
     * <strong>Use this method only if you DO NOT NEED to manipulate the debugging session.</strong>
     * @param projectId A project id.
     * @return a DebuggingSession if there exists one or <code>null</code> otherwise.
     */
    @Nullable private DebuggingSession getDebuggingSession(@Nonnull ProjectId projectId) {
        return this.debuggingSessions.get(projectId);
    }

    /**
     * Prepares everything that is necessary for a debugging session and returns a new instance of a DebuggingSession.
     *
     * @param projectId The project the debugging session belongs to.
     * @param userId The owner of the debugging session.
     * @return A new debugging session.
     */
    @Nonnull private DebuggingSession createDebuggingSession(@Nonnull ProjectId projectId, @Nonnull UserId userId) {
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

        return new DebuggingSession(projectId, userId, diagnosisEngine);
    }

}
