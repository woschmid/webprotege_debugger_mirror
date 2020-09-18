package edu.stanford.bmir.protege.web.server.debugger;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import edu.stanford.bmir.protege.web.server.project.ProjectDisposablesManager;
import edu.stanford.bmir.protege.web.server.renderer.RenderingManager;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.HasDispose;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.SessionState;
import edu.stanford.bmir.protege.web.shared.dispatch.ActionExecutionException;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import org.exquisite.core.DiagnosisException;
import org.exquisite.core.IExquisiteProgressMonitor;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.solver.ISolver;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
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
public class DebuggingSession implements HasDispose {

    private static final Logger logger = LoggerFactory.getLogger(DebuggingSession.class);

    /** Debugging session id */
    final private String id;

    /** The project the debugging session belongs to */
    final ProjectId projectId;

    /** The owner of this debugging session */
    private UserId userId;

    /** The current state of the debugging session */
    private SessionState state;

    /** The diagnosis engine used for this debugging session*/
    private IDiagnosisEngine<OWLLogicalAxiom> engine;

    /** Remember the query generated because of some diagnoses */
    private Query<OWLLogicalAxiom> query;

    /** Remember the diagnoses that were the base for the generated query */
    private Set<Diagnosis<OWLLogicalAxiom>> diagnoses;

    private RenderingManager renderingManager;

    private RevisionManager revisionManager;

    private IExquisiteProgressMonitor monitor;

    private OWLObjectRenderer owlObjectRenderer;

    @Inject
    public DebuggingSession(@Nonnull ProjectId projectId, @Nonnull RevisionManager revisionManager, @Nonnull RenderingManager renderingManager, @Nonnull OWLObjectRenderer renderer, @Nonnull ProjectDisposablesManager disposablesManager) {
        id = UUID.randomUUID().toString();

        this.projectId = projectId;
        this.revisionManager = revisionManager;
        this.renderingManager = renderingManager;
        this.owlObjectRenderer = renderer;

        this.state = SessionState.INIT;
        this.query = null;
        this.diagnoses = null;

        disposablesManager.register(this);
        logger.info("{} created", this);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("DebuggingSession").addValue(id).toString();
    }

    /**
     * @return the owner of this debugging session.
     */
    public UserId getUserId() {
        return userId;
    }

    /**
     * @return the current state the debugging session is in
     */
    public SessionState getState() {
        return state;
    }

    public Query<OWLLogicalAxiom> getQuery() {
        return query;
    }

    public Set<Diagnosis<OWLLogicalAxiom>> getDiagnoses() {
        return diagnoses;
    }

    public DiagnosisModel<OWLLogicalAxiom> getDiagnosisModel() {
        return engine.getSolver().getDiagnosisModel();
    }

    public RenderingManager getRenderingManager() {
        return renderingManager;
    }

    /**
     * Starts a new debugging session.
     *
     * @param userId The user who wants to start a debugging session.
     */
    public DebuggingSessionStateResult start(@Nonnull UserId userId) {
        synchronized (this) {
            if (getUserId() != null)
                throw new ActionExecutionException(new RuntimeException("A debugging session is already running for this project by user " + getUserId()));

            // guarantee that the session state is either in INIT or STOPPED state
            // let's not allow to start an already started session
            if (! (state==SessionState.INIT || state==SessionState.STOPPED) )
                throw new ActionExecutionException(new RuntimeException("Debugging session has been started already and cannot be started again"));


            this.userId = userId;
            this.state = SessionState.STARTED;
            this.monitor = new LoggingQueryProgressMonitor(this);

            final OWLOntologyManager ontologyManager = revisionManager.getOntologyManagerForRevision(revisionManager.getCurrentRevision());
            final List<OWLOntology> ontologies = new ArrayList<>(ontologyManager.getOntologies());
            if (ontologies.size() != 1)
                throw new ActionExecutionException(new RuntimeException("Expected only one ontology but there are " + ontologies.size()));

            final OWLOntology ontology = ontologies.get(0);
            logger.info("Found ontology {} from current revision {})", ontology, revisionManager.getCurrentRevision());


            logger.info("{} initiating for {} in {} ...", this, userId, projectId);

            // creating a solver includes a possibly long-lasting consistency and coherency check
            this.state = SessionState.COMPUTING;

            // creates a solver instance  using the ontology
            final ISolver<OWLLogicalAxiom> solver = SolverFactory.getSolver(ontology, this);
            logger.info("{} Solver created: {}", this, solver);

            // let's go back to init state after the solver has been created and a consistency and coherency check are done
            this.state = SessionState.STARTED;

            // create diagnosis engine using the solver
            this.engine = DiagnosisEngineFactory.getDiagnosisEngine(solver);
            logger.info("{} Diagnosis engine created: {}", this, this.engine);

            logger.info("{} started for {} in {}", this, userId, projectId);

            return calculateQuery(userId,null);
        }
    }

    /**
     * Submits the answers of the previous query if <code>not null</code> and calculates a new query.
     *
     * @param userId he user who wants to submit the answers and get the next query.
     * @param answers String representations of axioms from previous queries and their answers if given. Can be <code>null</code>.
     * @return A result for the front end representing the current state of the backend.
     */
    public DebuggingSessionStateResult calculateQuery(@Nonnull UserId userId, @Nullable ImmutableMap<String, Boolean> answers) {
        try {
            if (!userId.equals(getUserId()))
                throw new ActionExecutionException(new RuntimeException("A debugging session is already running for this project by user " + getUserId()));

            // verify that the session state in STARTED state
            if (state != SessionState.STARTED)
                throw new RuntimeException("Debugging session is in unexpected state " + state + " and thus query calculation is not allowed.");

            // reset the engine
            engine.resetEngine();

            // add possible answers
            // when we start a new session, then answers will be null,
            // otherwise this map should have at least one element
            if (answers != null && !answers.isEmpty())
                addAnswersToDiagnosisModel(answers);

            state = SessionState.COMPUTING;
            // calculate diagnoses
            Set<Diagnosis<OWLLogicalAxiom>> newDiagnoses = engine.calculateDiagnoses();
            logger.info("{} got {} diagnoses: {}", this, newDiagnoses.size(), newDiagnoses);

            if (newDiagnoses.size() >= 2) {
                // there are more than one diagnoses, therefore we need to create a query for the user to narrow down the problem.

                // configure and calculate the query
                final HeuristicQueryComputation<OWLLogicalAxiom> queryComputation =
                        new HeuristicQueryComputation<>(
                                new HeuristicConfiguration<>((AbstractDiagnosisEngine<OWLLogicalAxiom>) engine, monitor));

                logger.info("{} calculating query from {} diagnoses ...", this, newDiagnoses.size());
                queryComputation.initialize(newDiagnoses);

                // get the calculated query
                if (queryComputation.hasNext()) {
                    query = queryComputation.next();
                    diagnoses = newDiagnoses;
                    state = SessionState.STARTED;
                    logger.info("{} calculated query {}", this, query);
                    return DebuggingResultFactory.getDebuggingSessionStateResult(this);
                } else {
                    throw new RuntimeException("No query could be calculated.");
                }
            } else if (newDiagnoses.size() == 1) {
                // we found the diagnosis with the faulty axioms
                // therefore there is no query generation necessary anymore

                logger.info("{} found final diagnosis! {}", this, newDiagnoses);
                query = null;
                diagnoses = newDiagnoses;
                state = SessionState.STARTED;
                return DebuggingResultFactory.getDebuggingSessionStateResult(this);
            } else {
                logger.info("{} no diagnoses found -> ontology is consistent and coherent!", this);
                // diagnoses.size() == 0, the ontology is consistent and coherent and has therefore no diagnoses
                query = null;
                diagnoses = null;
                state = SessionState.STARTED;
                return DebuggingResultFactory.getDebuggingSessionStateResult(this);
            }
        } catch (RuntimeException | DiagnosisException e) {
            throw new ActionExecutionException(e);
        }
    }

    /**
     * Stops the running debugging session.
     *
     * @param userId The user who wants to stop a debugging session.
     */
    public DebuggingSessionStateResult stop(@Nonnull UserId userId) {
        if (!userId.equals(getUserId()))
            throw new ActionExecutionException(new RuntimeException("A debugging session is already running for this project by user " + getUserId()));
        stop();
        return DebuggingResultFactory.getDebuggingSessionStateResult(this);
    }

    /**
     * Stops a debugging session, which can be restarted later.
     */
    private void stop() {
        // dispose diagnosis engine, solver and everything else...
        // No check will be done on the session state beforehand.
        state = SessionState.STOPPED;
        userId = null;
        engine.dispose();
        monitor = null;
        query = null;
        diagnoses = null;
    }

    @Override
    public void dispose() {
        logger.info("{} disposing ...", this);
        stop();
        renderingManager = null;
        owlObjectRenderer = null;
        revisionManager = null;
        engine = null;
    }

    /**
     * Adds answers that were given to the previous query to the diagnosis model either as positive or as negative test cases.
     *
     * @param answers String representations of axioms from the previous query and the answers (either true or false) given to them.
     */
    private void addAnswersToDiagnosisModel(@Nonnull ImmutableMap<String, Boolean> answers) {
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = engine.getSolver().getDiagnosisModel();
        for (Map.Entry<String, Boolean> answer : answers.entrySet()) {
            // look up the axiom representing this string
            final OWLLogicalAxiom axiom = lookupAxiomFromPreviousQuery(answer.getKey());
            if (answer.getValue()) {
                diagnosisModel.getEntailedExamples().add(axiom);
                logger.info("{} got positive answer for {}", this, axiom);
            } else {
                diagnosisModel.getNotEntailedExamples().add(axiom);
                logger.info("{} got negative answer for {}", this, axiom);
            }
        }
    }

    /**
     * Get the axiom representing the string.
     *
     * @param axiomAsString A string representation of an axiom to look up in the previous query.
     * @return The OWLLogicalAxiom instance representing the string.
     * @throws ActionExecutionException if no axiom representing this string can be found from the previous query.
     */
    private OWLLogicalAxiom lookupAxiomFromPreviousQuery(@Nonnull final String axiomAsString) {

        // we expect that the query stated before does exist
        if (query == null) {
            throw new ActionExecutionException(new RuntimeException("No previous query instance found"));
        }

        // lookup the matching axiom
        for (OWLLogicalAxiom a : query.formulas)
            if (axiomAsString.equals(owlObjectRenderer.render(a).trim())) // trimming is necessary for EquivalentClasses (sic)
                return a;

        // if no axiom could be found, we have to throw an exception
        throw new ActionExecutionException(new RuntimeException(axiomAsString + " could not be not found"));
    }

}
