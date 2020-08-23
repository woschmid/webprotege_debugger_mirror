package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.shared.debugger.DebuggingResult;
import edu.stanford.bmir.protege.web.shared.dispatch.ActionExecutionException;
import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;

import static edu.stanford.bmir.protege.web.server.debugger.DebuggingSessionManagerImpl.createDebuggingResult;

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

    protected void dispose() {
        // dispose diagnosis engine, solver and everything else...
        engine.dispose();
        state = SessionState.INVALID;
    }

}
