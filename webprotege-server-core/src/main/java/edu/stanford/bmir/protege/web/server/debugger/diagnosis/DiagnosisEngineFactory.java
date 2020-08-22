package edu.stanford.bmir.protege.web.server.debugger.diagnosis;

import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.engines.HSTreeEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.solver.ISolver;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

public class DiagnosisEngineFactory {

    /**
     * Creates a HSTree Diagnosis engine with QuickXPlain as conflict searcher.
     *
     * @param solver A solver to be attached to returned diagnosis engine.
     * @return A diagnosis engine.
     */
    public static IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ISolver<OWLLogicalAxiom> solver) {
        return new HSTreeEngine<>(solver, new QuickXPlain<>(solver));
    }
}
