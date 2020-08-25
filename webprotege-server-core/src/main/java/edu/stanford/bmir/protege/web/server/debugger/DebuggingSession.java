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
import java.util.List;
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

            // calculate diagnoses
            final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = engine.calculateDiagnoses();
            logger.info("got {} diagnoses: {}", diagnoses.size(), diagnoses);

            // calculate queries
            if (diagnoses.size() >= 2) {
                HeuristicConfiguration<OWLLogicalAxiom> heuristicConfiguration = new HeuristicConfiguration<>((AbstractDiagnosisEngine) engine, null);
                final HeuristicQueryComputation<OWLLogicalAxiom> queryComputation = new HeuristicQueryComputation<>(heuristicConfiguration);

                queryComputation.initialize(diagnoses);

                if (queryComputation.hasNext()) {
                    final Query<OWLLogicalAxiom> query = queryComputation.next();
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
            final OWLLogicalAxiom axiom = lookupAxiom(diagnosisModel.getPossiblyFaultyFormulas(), answer.getKey());
            if (axiom != null)
                if (answer.getValue()) {
                    diagnosisModel.getEntailedExamples().add(axiom);
                    logger.info("got positive answer for {}", axiom);
                } else {
                    diagnosisModel.getNotEntailedExamples().add(axiom);
                    logger.info("got negative answer for {}", axiom);
                }
            else
                logger.warn("no axiom found for {}", answer.getKey());
        }
    }

    private static OWLLogicalAxiom lookupAxiom(@Nonnull List<OWLLogicalAxiom> possiblyFaultyFormulas, @Nonnull String key) {
        for (OWLLogicalAxiom a : possiblyFaultyFormulas) {
            String axiom = a.toString();
            logger.info("Is {} matching {} ?", axiom, key);
            if (key.equals(a.toString())) return a;
        }
        return null;
    }

}
