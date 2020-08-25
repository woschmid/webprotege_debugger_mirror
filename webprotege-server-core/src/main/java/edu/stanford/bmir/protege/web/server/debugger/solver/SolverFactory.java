package edu.stanford.bmir.protege.web.server.debugger.solver;

import edu.stanford.bmir.protege.web.shared.dispatch.ActionExecutionException;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ConsistencyChecker;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.exquisite.core.solver.ISolver;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;

public class SolverFactory {

    public static ISolver<OWLLogicalAxiom> getSolver(OWLOntology ontology) throws ActionExecutionException {

        // HermiT reasoner factory
        final ReasonerFactory reasonerFactory = new ReasonerFactory();

        // creates a diagnosis model from the ontology
        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = ExquisiteOWLReasoner.generateDiagnosisModel(ontology, null);

        try {
            diagnosisModel = ConsistencyChecker.checkConsistency(ontology, diagnosisModel, reasonerFactory, true, true, null, null).getDiagnosisModel();
            // creates a reasoner using HermiT and the diagnosis model from the ontology provided
            ExquisiteOWLReasoner solver = new ExquisiteOWLReasoner(diagnosisModel, reasonerFactory);
            solver.setEntailmentTypes(InferenceType.CLASS_HIERARCHY, InferenceType.DISJOINT_CLASSES);
            return solver;
        } catch (OWLOntologyCreationException e) {
            throw new ActionExecutionException(e);
        }
    }
}
