package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.shared.dispatch.ActionExecutionException;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ConsistencyCheckResult;
import org.exquisite.core.solver.ConsistencyChecker;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.exquisite.core.solver.ISolver;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import javax.annotation.Nonnull;

/**
 * A solver factory using official reasoners (HermiT, Pellet or JFact).
 * If you want to add a new reasoner, add it's artifact to this module's pom.xml modify the code below.
 */
public class SolverFactory {

    @Nonnull public static ISolver<OWLLogicalAxiom> getSolver(@Nonnull OWLOntology ontology,
                                                     @Nonnull DiagnosisModel<OWLLogicalAxiom> diagnosisModel,
                                                     @Nonnull DebuggingSession debuggingSession) throws ActionExecutionException {

        // choose between the available reasoners below!
        @Nonnull OWLReasonerFactory reasonerFactory;

        // Select between different popular reasoners here - recompilation necessary!

        // HermiT Reasoner Factory
        // reasonerFactory = new org.semanticweb.HermiT.ReasonerFactory();

        // Pellet Reasoner Factory (pellet-owlapi-ignazio1977/2.4.0-ignazio1977)
        reasonerFactory = new com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory();

        // JFact DL Reasoner Factory (http://jfact.sourceforge.net/)
        // reasonerFactory = new uk.ac.manchester.cs.jfact.JFactFactory();

        try {

            // check the consistency and coherency of the ontology
            final ConsistencyCheckResult consistency = ConsistencyChecker.checkConsistency(ontology, diagnosisModel, reasonerFactory, true, true, null, new LoggingReasonerProgressMonitor(debuggingSession));

            // when not debugging is necessary, we tell it the user with an exception (only when used as non test-driven debugging)
            // if (consistency.isCoherent() == Boolean.TRUE && consistency.isCoherent() == Boolean.TRUE)
            //    throw new RuntimeException("The ontology is CONSISTENT and COHERENT. No debugging possible!");

            // get the diagnosis model
            diagnosisModel = consistency.getDiagnosisModel();

            // create a reasoner using a reasoner factory and the diagnosis model from the ontology provided
            final ExquisiteOWLReasoner solver = new ExquisiteOWLReasoner(diagnosisModel, reasonerFactory, new LoggingReasonerProgressMonitor(debuggingSession));

            // define the entailment types
            solver.setEntailmentTypes(InferenceType.CLASS_HIERARCHY, InferenceType.DISJOINT_CLASSES);

            // return the solver
            return solver;
        } catch (RuntimeException | OWLOntologyCreationException e) {
            throw new ActionExecutionException(e);
        }
    }
}
