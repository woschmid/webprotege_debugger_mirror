package edu.stanford.bmir.protege.web.server.debugger;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.SessionState;
import edu.stanford.bmir.protege.web.shared.dispatch.ActionExecutionException;
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
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class DebuggingSession {

    private static final Logger logger = LoggerFactory.getLogger(DebuggingSession.class);

    /** Debugging session id */
    final private String id;

    /** The project the debugging session belongs to */
    final ProjectId projectId;

    /** The owner of this debugging session */
    final private UserId userId;

    /** The current state of the debugging session */
    private SessionState state;

    /** The diagnosis engine used for this debugging session*/
    private IDiagnosisEngine<OWLLogicalAxiom> engine;

    /** Remember the query generated because of some diagnoses */
    private Query<OWLLogicalAxiom> query;

    /** Remember the diagnoses that were the base for the generated query */
    private Set<Diagnosis<OWLLogicalAxiom>> diagnoses;

    private IExquisiteProgressMonitor monitor;

    /**
     * @param projectId The project the debugging session belongs to.
     * @param userId The owner/creator of the debugging session.
     */
    protected DebuggingSession(ProjectId projectId, UserId userId) {
        id = projectId.getId() + "-" + userId.getUserName();
        this.projectId = projectId;
        this.userId = userId;
        this.state = SessionState.INIT;
        this.query = null;
        this.diagnoses = null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("DebuggingSession").addValue(id).toString();
    }

    /**
     * @return the owner of this debugging session.
     */
    protected UserId getUserId() {
        return userId;
    }

    /**
     * @return the current state the debugging session is in
     */
    protected SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    protected Query<OWLLogicalAxiom> getQuery() {
        return query;
    }

    protected Set<Diagnosis<OWLLogicalAxiom>> getDiagnoses() {
        return diagnoses;
    }

    protected DiagnosisModel<OWLLogicalAxiom> getDiagnosisModel() {
        return engine.getSolver().getDiagnosisModel();
    }

    public void setEngine(IDiagnosisEngine<OWLLogicalAxiom> engine) {
        this.engine = engine;
    }

    /**
     * Starts a new debugging session.
     */
    protected void start() {
        // guarantee that the session state is either in INIT or STOPPED state
        // let's not allow to start an already started session
        if (state == SessionState.STARTED) {
            stop();
            throw new ActionExecutionException(new RuntimeException("Debugging session has been started already and cannot be started again"));
        }
        this.state = SessionState.STARTED;
        this.query = null;
        this.monitor = new LoggingQueryProgressMonitor();
    }

    /**
     * Calculates a new query depending on the given answers.
     *
     * @param answers String representations of axioms from previous queris and their answers (true or false).
     * @return A result for the front end representing the current state of the backend.
     */
    protected DebuggingSessionStateResult calculateQuery(@Nullable ImmutableMap<String, Boolean> answers) {
        try {
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
            stop();
            throw new ActionExecutionException(e);
        }
    }

    /**
     * Stops a started debugging session.
     */
    protected void stop() {
        // dispose diagnosis engine, solver and everything else...
        // No check will be done on the session state beforehand.
        state = SessionState.STOPPED;
        engine.dispose();
        query = null;
        diagnoses = null;
        monitor = null;
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
    private OWLLogicalAxiom lookupAxiomFromPreviousQuery(@Nonnull String axiomAsString) {

        // we expect that the query stated before does exist
        if (query == null) {
            stop();
            throw new ActionExecutionException(new RuntimeException("No previous query instance found"));
        }

        // lookup the matching axiom
        for (OWLLogicalAxiom a : query.formulas)
            if (axiomAsString.equals(ManchesterSyntaxRenderer.renderAxiom(a)))
                return a;

        // if no axiom could be found, we have to throw an exception
        stop();
        throw new ActionExecutionException(new RuntimeException(axiomAsString + " could not be not found"));
    }

}
