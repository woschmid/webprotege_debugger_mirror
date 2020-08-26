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

    enum SessionState {INIT, STARTED, STOPPED , FAILED}

    final private String id;

    private SessionState state;

    final private IDiagnosisEngine<OWLLogicalAxiom> engine;

    /** Remember the query for the answering process in the next step */
    private Query<OWLLogicalAxiom> query;

    protected DebuggingSession(IDiagnosisEngine<OWLLogicalAxiom> engine) {
        id = UUID.randomUUID().toString();
        this.engine = engine;
        this.state = SessionState.INIT;
    }

    /**
     * Starts a new debugging session.
     */
    protected void start() {
        this.state = SessionState.STARTED;
        // reset the engine
        engine.resetEngine();
    }

    /**
     * Calculates a new query depending on the given answers.
     *
     * @param answers String representations of axioms from previous queris and their answers (true or false).
     * @return A result for the front end representing the current state of the backend.
     */
    protected DebuggingResult calc(@Nullable ImmutableMap<String, Boolean> answers) {
        try {
            if (state != SessionState.STARTED)
                throw new DiagnosisException("Debugging session is in invalid state (state: " + state + ")");

            // reset the engine
            engine.resetEngine();

            // add possible answers
            if (answers != null && !answers.isEmpty())
                addAnswersToDiagnosisModel(answers);

            // delete the previous query - we do not need them anymore for answer lookup now
            query = null;

            // calculate diagnoses
            final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = engine.calculateDiagnoses();
            logger.info("got {} diagnoses: {}", diagnoses.size(), diagnoses);

            // calculate queries
            if (diagnoses.size() >= 2) {
                final HeuristicConfiguration<OWLLogicalAxiom> heuristicConfiguration = new HeuristicConfiguration<>((AbstractDiagnosisEngine) engine, null);
                final HeuristicQueryComputation<OWLLogicalAxiom> queryComputation = new HeuristicQueryComputation<>(heuristicConfiguration);

                queryComputation.initialize(diagnoses);

                if (queryComputation.hasNext()) {
                    query = queryComputation.next();
                    logger.info("computed query {}", query);
                    return DebuggingResultFactory.getDebuggingResult(query, diagnoses, engine.getSolver().getDiagnosisModel());
                } else {
                    logger.info("no query computed");
                    return DebuggingResultFactory.getDebuggingResult(null, diagnoses, engine.getSolver().getDiagnosisModel());
                }
            } else if (diagnoses.size() == 1) {
                // we are finished!
                return DebuggingResultFactory.getDebuggingResult(null, diagnoses, engine.getSolver().getDiagnosisModel());
            } else {
                // this is an unexpected case and should not occur
                throw new ActionExecutionException(new RuntimeException("Unexpected number of " + diagnoses.size() + " diagnoses."));
            }
        } catch (DiagnosisException e) {
            state = SessionState.FAILED;
            throw new ActionExecutionException(e);
        }
    }

    /**
     * Stops a started debugging session.
     */
    protected void stop() {
        // dispose diagnosis engine, solver and everything else...
        engine.dispose();
        state = SessionState.STOPPED;
    }

    /**
     * Adds answers from the previous query to the diagnosis model as positive and negative test cases.
     *
     * @param answers String representations of axioms from previous queris and their answers (true or false).
     */
    private void addAnswersToDiagnosisModel(@Nonnull ImmutableMap<String, Boolean> answers) {
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = engine.getSolver().getDiagnosisModel();
        for (Map.Entry<String, Boolean> answer : answers.entrySet()) {
            // look up the axiom representing this string
            final OWLLogicalAxiom axiom = lookupAxiomFromPreviousQuery(answer.getKey());
            if (answer.getValue()) {
                diagnosisModel.getEntailedExamples().add(axiom);
                logger.info("got positive answer for {}", axiom);
            } else {
                diagnosisModel.getNotEntailedExamples().add(axiom);
                logger.info("got negative answer for {}", axiom);
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
        if (query == null) {
            state = SessionState.FAILED;
            throw new ActionExecutionException(new RuntimeException("No previous query instance found"));
        }

        for (OWLLogicalAxiom a : query.formulas) // lookup
            if (axiomAsString.equals(a.toString()))
                return a;

        // if no axiom could be found, we have to throw an exception
        state = SessionState.FAILED;
        throw new ActionExecutionException(new RuntimeException(axiomAsString + " could not be not found"));
    }

}
