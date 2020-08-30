package edu.stanford.bmir.protege.web.server.debugger;

import com.google.common.collect.ImmutableMap;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingResult;
import edu.stanford.bmir.protege.web.shared.dispatch.ActionExecutionException;
import org.exquisite.core.DiagnosisException;
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
import java.util.UUID;

public class DebuggingSession {

    private static final Logger logger = LoggerFactory.getLogger(DebuggingSession.class);

    /**
     * INIT ... session has been created not not yet started<br/>
     * STARTED ... session has been started manually<br/>
     * STOPPED ... session has been stopped manually<br/>
     */
    enum SessionState {INIT, STARTED, STOPPED}

    final private String id;

    private SessionState state;

    final private IDiagnosisEngine<OWLLogicalAxiom> engine;

    /** Remember the query for the answering process in the next step */
    private Query<OWLLogicalAxiom> query;

    private LoggingQueryProgressMonitor monitor;

    protected DebuggingSession(IDiagnosisEngine<OWLLogicalAxiom> engine) {
        id = UUID.randomUUID().toString();
        this.engine = engine;
        this.state = SessionState.INIT;
        this.query = null;
    }

    @Override
    public String toString() { return "DebuggingSession{" + id + '}'; }

    /**
     * Starts a new debugging session.
     */
    protected void start() {
        // guarantee that the session state is either in INIT, STOPPED or FAILED
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
    protected DebuggingResult calculateQuery(@Nullable ImmutableMap<String, Boolean> answers) {
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

            // delete the previous query - we do not need them anymore for answer lookup now
            query = null;

            // calculate diagnoses
            final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = engine.calculateDiagnoses();
            logger.info("{} got {} diagnoses: {}", this, diagnoses.size(), diagnoses);

            if (diagnoses.size() >= 2) {
                // there are more than one diagnoses, therefore we need to create a query for the user to narrow down the problem.

                // configure and calculate the query
                final HeuristicQueryComputation<OWLLogicalAxiom> queryComputation =
                        new HeuristicQueryComputation<>(
                                new HeuristicConfiguration<>((AbstractDiagnosisEngine<OWLLogicalAxiom>) engine, monitor));

                logger.info("{} calculating query from {} diagnoses ...", this, diagnoses.size());
                queryComputation.initialize(diagnoses);

                // get the calculated query
                if (queryComputation.hasNext()) {
                    query = queryComputation.next();
                    logger.info("{} calculated query {}", this, query);
                    return DebuggingResultFactory.getDebuggingResult(query, diagnoses, engine.getSolver().getDiagnosisModel());
                } else {
                    throw new RuntimeException("No query could be calculated.");
                }
            } else if (diagnoses.size() == 1) {
                // we found the diagnosis with the faulty axioms
                logger.info("{} found final diagnosis! {}", this, diagnoses);
                return DebuggingResultFactory.getDebuggingResult(null, diagnoses, engine.getSolver().getDiagnosisModel());
            } else {
                logger.info("{} no diagnoses found -> ontology is consistent and coherent!", this);
                // diagnoses.size() == 0, the ontology is consistent and coherent and has therefore no diagnoses
                return DebuggingResultFactory.getDebuggingResult(null, null, engine.getSolver().getDiagnosisModel());
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
