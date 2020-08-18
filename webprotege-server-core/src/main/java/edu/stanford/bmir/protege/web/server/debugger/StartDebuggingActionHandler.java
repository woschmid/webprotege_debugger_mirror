package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingResult;
import edu.stanford.bmir.protege.web.shared.debugger.Diagnosis;
import edu.stanford.bmir.protege.web.shared.debugger.StartDebuggingAction;
import edu.stanford.bmir.protege.web.shared.debugger.TestCase;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.exquisite.core.DiagnosisException;
import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.engines.HSTreeEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.solver.ConsistencyChecker;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.exquisite.core.solver.ISolver;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class StartDebuggingActionHandler extends AbstractProjectActionHandler<StartDebuggingAction, DebuggingResult> {

    private static final Logger logger = LoggerFactory.getLogger(StartDebuggingActionHandler.class);

    @Nonnull
    private final RevisionManager revisionManager;

    @Nonnull
    private final ProjectId projectId;

    @Inject
    public StartDebuggingActionHandler(@Nonnull AccessManager accessManager,
                                       @Nonnull RevisionManager revisionManager,
                                       @Nonnull ProjectId projectId) {
        super(accessManager);
        this.revisionManager = checkNotNull(revisionManager);
        this.projectId = checkNotNull(projectId);
    }

    @Nonnull
    @Override
    public Class<StartDebuggingAction> getActionClass() {
        return StartDebuggingAction.class;
    }

    @Nonnull
    @Override
    public DebuggingResult execute(@Nonnull StartDebuggingAction action, @Nonnull ExecutionContext executionContext) {

        // TODO CREATE A MANAGER CLASS THAT IS RESPONSIBLE FOR THE DEBUGGING SESSION LIFE CYCLE

        // THE FOLLOWING CODE IS JUST A SCRATCHPAD FOR SOLVER AND DIAGNOSISENGINE CREATION AND USAGE FOR DIAGNOSIS CALCULATION AND QUERY GENERATION AND SHOULD BE DELEGATED TO A SEPARATE CLASS
        IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = null;
        ISolver<OWLLogicalAxiom> solver = null;

        try {

            final OWLOntologyManager owlOntologyManager = this.revisionManager.getOntologyManagerForRevision(this.revisionManager.getCurrentRevision());
            final List<OWLOntology> ontologies = new ArrayList<>(owlOntologyManager.getOntologies());
            if (ontologies.size() == 1) {
                final OWLOntology ontology = ontologies.get(0);
                logger.info("{} has ontology {})", projectId, ontology);

                // create a solver using the ontology
                solver = createSolver(ontology);
                logger.info("{} solver created {}", projectId, solver);

                // create diagnosis engine using the solver
                diagnosisEngine = getDiagnosisEngine(solver);
                logger.info("{} diagnosis engine created: {}", projectId, diagnosisEngine);

                // prepare for diagnosis calculation
                diagnosisEngine.resetEngine();
                diagnosisEngine.setMaxNumberOfDiagnoses(0); // 0 means calculate all possible diagnoses

                // calculate diagnoses
                final Set<org.exquisite.core.model.Diagnosis<OWLLogicalAxiom>> diagnoses = diagnosisEngine.calculateDiagnoses();
                logger.info("{} got {} diagnoses: {}", projectId, diagnoses.size(), diagnoses);

                // calculate queries
                if (diagnoses.size() >= 2) {
                    HeuristicConfiguration<OWLLogicalAxiom> heuristicConfiguration = new HeuristicConfiguration<>((AbstractDiagnosisEngine)diagnosisEngine, null);
                    final HeuristicQueryComputation<OWLLogicalAxiom> queryComputation = new HeuristicQueryComputation<>(heuristicConfiguration);

                    queryComputation.initialize(diagnoses);

                    if (queryComputation.hasNext()) {
                        final Query<OWLLogicalAxiom> query = queryComputation.next();
                        logger.info("{} computed query {}", projectId, query);
                        return createDebuggingResult(query, diagnoses, solver.getDiagnosisModel());
                    } else {
                        logger.info("{} no query computed", projectId);
                        return createDebuggingResult(null, diagnoses, solver.getDiagnosisModel());
                    }
                } else {
                    return createDebuggingResult(null, diagnoses, solver.getDiagnosisModel());
                }

            } else {
                logger.error("Only one ontology expected but there were {} ongologies", ontologies.size());
                return createDebuggingResult(null, null, null);
            }
        } catch (RuntimeException | DiagnosisException | OWLOntologyCreationException e) {
            logger.error(e.getMessage(), e);
            return createDebuggingResult(null, null,null);
        } finally {
            // dispose diagnosis engine, solver and everything else...
            if (diagnosisEngine!=null)
                diagnosisEngine.dispose();
            else
                if (solver!=null) solver.dispose();
        }
    }

    private IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ISolver<OWLLogicalAxiom> solver) {
        return new HSTreeEngine<>(solver, new QuickXPlain<>(solver));
    }

    private ExquisiteOWLReasoner createSolver(OWLOntology ontology) throws OWLOntologyCreationException {

        // HermiT reasoner factory
        final ReasonerFactory reasonerFactory = new ReasonerFactory();

        // create a diagnosis model from the ontology
        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = ExquisiteOWLReasoner.generateDiagnosisModel(ontology, null);
        diagnosisModel = ConsistencyChecker.checkConsistency(ontology, diagnosisModel, reasonerFactory, true, true, null, null).getDiagnosisModel();

        // creates a reasoner using HermiT and the diagnosis model from the ontology provided
        return new ExquisiteOWLReasoner(diagnosisModel, reasonerFactory);
    }

    @Nonnull
    private DebuggingResult createDebuggingResult(@Nullable Query<OWLLogicalAxiom> query, @Nullable Set<org.exquisite.core.model.Diagnosis<OWLLogicalAxiom>> diagnoses, @Nullable DiagnosisModel<OWLLogicalAxiom> diagnosisModel) {

        edu.stanford.bmir.protege.web.shared.debugger.Query q = null;
        List<Diagnosis> d = new ArrayList<>();
        List<TestCase> p = new ArrayList<>();
        List<TestCase> n = new ArrayList<>();

        if (query != null)
            q = new edu.stanford.bmir.protege.web.shared.debugger.Query(query.formulas);

        if (diagnoses != null) {
            for (org.exquisite.core.model.Diagnosis<OWLLogicalAxiom> diag : diagnoses)
                d.add(new Diagnosis(diag.getFormulas()));
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
