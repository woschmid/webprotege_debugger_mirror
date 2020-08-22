package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.shared.debugger.DebuggingResult;
import edu.stanford.bmir.protege.web.shared.debugger.TestCase;
import edu.stanford.bmir.protege.web.shared.dispatch.ActionExecutionException;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DebuggingSession {

    private static final Logger logger = LoggerFactory.getLogger(DebuggingSession.class);

    enum SessionState {INIT, RUNNING, STOPPED, FAILED}

    private String id;

    private ProjectId projectId;

    private SessionState state = SessionState.INIT;

    private IDiagnosisEngine<OWLLogicalAxiom> engine;

    public DebuggingSession(ProjectId projectId, IDiagnosisEngine<OWLLogicalAxiom> engine) {
        id = UUID.randomUUID().toString();
        this.projectId = projectId;
        this.engine = engine;
    }

    public IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine() {
        return engine;
    }

    public String getId() {
        return id;
    }

    public SessionState getState() {
        return state;
    }

    public void reset() {
        // prepare for diagnosis calculation
        engine.resetEngine();
        engine.setMaxNumberOfDiagnoses(0); // 0 means calculate all possible diagnoses
        state = SessionState.RUNNING;
    }

    public DebuggingResult calc() {
        try {
            // calculate diagnoses
            final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = engine.calculateDiagnoses();
            logger.info("{} got {} diagnoses: {}", projectId, diagnoses.size(), diagnoses);

            // calculate queries
            if (diagnoses.size() >= 2) {
                HeuristicConfiguration<OWLLogicalAxiom> heuristicConfiguration = new HeuristicConfiguration<>((AbstractDiagnosisEngine) engine, null);
                final HeuristicQueryComputation<OWLLogicalAxiom> queryComputation = new HeuristicQueryComputation<>(heuristicConfiguration);

                queryComputation.initialize(diagnoses);

                if (queryComputation.hasNext()) {
                    final Query<OWLLogicalAxiom> query = queryComputation.next();
                    logger.info("{} computed query {}", projectId, query);
                    return createDebuggingResult(query, diagnoses, engine.getSolver().getDiagnosisModel());
                } else {
                    logger.info("{} no query computed", projectId);
                    return createDebuggingResult(null, diagnoses, engine.getSolver().getDiagnosisModel());
                }
            } else {
                return createDebuggingResult(null, diagnoses, engine.getSolver().getDiagnosisModel());
            }
        } catch (DiagnosisException e) {
            throw new ActionExecutionException(e);
        }
    }

    public DebuggingResult dispose() {
        // dispose diagnosis engine, solver and everything else...
        engine.dispose();
        state = SessionState.STOPPED;
        return createDebuggingResult(null, null, null);
    }

    @Nonnull
    private DebuggingResult createDebuggingResult(@Nullable Query<OWLLogicalAxiom> query, @Nullable Set<org.exquisite.core.model.Diagnosis<OWLLogicalAxiom>> diagnoses, @Nullable DiagnosisModel<OWLLogicalAxiom> diagnosisModel) {

        edu.stanford.bmir.protege.web.shared.debugger.Query q = null;
        List<edu.stanford.bmir.protege.web.shared.debugger.Diagnosis> d = new ArrayList<>();
        List<TestCase> p = new ArrayList<>();
        List<TestCase> n = new ArrayList<>();

        if (query != null)
            q = new edu.stanford.bmir.protege.web.shared.debugger.Query(query.formulas);

        if (diagnoses != null) {
            for (org.exquisite.core.model.Diagnosis<OWLLogicalAxiom> diag : diagnoses)
                d.add(new edu.stanford.bmir.protege.web.shared.debugger.Diagnosis(diag.getFormulas()));
        }

        if (diagnosisModel != null)
            for (OWLLogicalAxiom a : diagnosisModel.getEntailedExamples())
                p.add(new TestCase(a));


        if (diagnosisModel != null)
            for (OWLLogicalAxiom a : diagnosisModel.getNotEntailedExamples())
                n.add(new TestCase(a));

        return new DebuggingResult(q, d, p, n);
    }


}
