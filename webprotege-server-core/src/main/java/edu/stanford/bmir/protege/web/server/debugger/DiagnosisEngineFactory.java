package edu.stanford.bmir.protege.web.server.debugger;

import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.engines.HSTreeEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

import javax.annotation.Nonnull;

public class DiagnosisEngineFactory {

    /**
     * Creates a HSTree Diagnosis engine with QuickXPlain as conflict searcher.
     *
     * @param diagnosisModel The diagosis model.
     * @param debuggingSession The debugging session.
     * @return A diagnosis engine.
     * @throws OWLOntologyCreationException If an error occurs.
     */
    public static IDiagnosisEngine<OWLLogicalAxiom> createDiagnosisEngine(@Nonnull DiagnosisModel<OWLLogicalAxiom> diagnosisModel,
                                                                          @Nonnull DebuggingSession debuggingSession) throws OWLOntologyCreationException {

        // create a reasoner using a reasoner factory and the diagnosis model from the ontology provided
        final ExquisiteOWLReasoner solver =
                new ExquisiteOWLReasoner(
                        diagnosisModel,
                        ReasonerFactory.getReasonerFactory(),
                        new SimpleConfiguration(
                                new LoggingReasonerProgressMonitor(debuggingSession),
                                debuggingSession.getPreferences().getReasonerTimeoutInMillis()),
                        new InferenceType[] {InferenceType.CLASS_HIERARCHY, InferenceType.DISJOINT_CLASSES}
        );

        // creates diagnosis engine using the solver
        final HSTreeEngine<OWLLogicalAxiom> engine = new HSTreeEngine<>(solver, new QuickXPlain<>(solver));
        engine.setMaxNumberOfDiagnoses(0);

        return engine;
    }
}
