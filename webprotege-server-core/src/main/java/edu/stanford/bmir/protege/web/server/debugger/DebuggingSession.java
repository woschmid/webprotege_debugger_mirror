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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static edu.stanford.bmir.protege.web.server.debugger.DebuggingSessionManager.createDebuggingResult;

public class DebuggingSession {

    private static final Logger logger = LoggerFactory.getLogger(DebuggingSession.class);

    enum SessionState {INVALID, IN_USE}

    final private String id;

    private SessionState state = SessionState.INVALID;

    final private IDiagnosisEngine<OWLLogicalAxiom> engine;

    protected DebuggingSession(IDiagnosisEngine<OWLLogicalAxiom> engine) {
        id = UUID.randomUUID().toString();
        this.engine = engine;
    }

    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine() {
        return engine;
    }

    protected String getId() {
        return id;
    }

    protected SessionState getState() {
        return state;
    }

    protected void reset() {
        // prepare for diagnosis calculation
        engine.resetEngine();
        engine.setMaxNumberOfDiagnoses(0); // 0 means calculate all possible diagnoses
        this.state = SessionState.IN_USE;
    }

    protected DebuggingResult calc() {

        try {
            if (state == SessionState.INVALID)
                throw new DiagnosisException("Debugging session state is invalid");

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
            throw new ActionExecutionException(e);
        }
    }

    protected void addAnswer(ImmutableMap<String, Boolean> answers) {
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
            logger.debug("Is {} matching {} ?", axiom, key);
            if (key.equals(a.toString())) return a;
        }
        return null;
    }

    protected void dispose() {
        // dispose diagnosis engine, solver and everything else...
        engine.dispose();
        state = SessionState.INVALID;
    }

}
