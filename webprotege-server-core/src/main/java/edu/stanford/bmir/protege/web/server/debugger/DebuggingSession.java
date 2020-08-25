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
import java.rmi.UnexpectedException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static edu.stanford.bmir.protege.web.server.debugger.DebuggingSessionManager.createDebuggingResult;

public class DebuggingSession {

    private static final Logger logger = LoggerFactory.getLogger(DebuggingSession.class);

    enum SessionState {INIT, STARTED, STOPPED , FAILED}

    final private String id;

    private SessionState state;

    final private IDiagnosisEngine<OWLLogicalAxiom> engine;

    private Query<OWLLogicalAxiom> query; // we save the query for the answering process in the next step

    protected DebuggingSession(IDiagnosisEngine<OWLLogicalAxiom> engine) {
        id = UUID.randomUUID().toString();
        this.engine = engine;
        this.state = SessionState.INIT;
    }

    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine() {
        return engine;
    }

    protected String getId() {
        return id;
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
     * @param answers
     * @return
     */
    protected DebuggingResult calc(@Nullable ImmutableMap<String, Boolean> answers) {
        try {
            if (state != SessionState.STARTED)
                throw new DiagnosisException("Debugging session in invalid state (state: " + state + ")");

            // reset the engine
            engine.resetEngine();

            // add possible answers
            if (answers != null)
                addAnswer(answers);

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
                    return createDebuggingResult(query, diagnoses, engine.getSolver().getDiagnosisModel());
                } else {
                    logger.info("no query computed");
                    return createDebuggingResult(null, diagnoses, engine.getSolver().getDiagnosisModel());
                }
            } else {
                return createDebuggingResult(null, diagnoses, engine.getSolver().getDiagnosisModel());
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

    private void addAnswer(ImmutableMap<String, Boolean> answers) {
        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = getDiagnosisEngine().getSolver().getDiagnosisModel();
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

    private OWLLogicalAxiom lookupAxiomFromPreviousQuery(@Nonnull String axiomAsString) {
        if (query == null)
            throw new ActionExecutionException(new UnexpectedException("No previous query instance found"));

        for (OWLLogicalAxiom a : query.formulas) // lookup
            if (axiomAsString.equals(a.toString()))
                return a;

        // here the axiom could not be found.
        state = SessionState.FAILED;
        throw new ActionExecutionException(new UnexpectedException(axiomAsString + " could not be not found"));
    }

}
