package edu.stanford.bmir.protege.web.server.debugger;

import org.exquisite.core.IExquisiteProgressMonitor;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ConsistencyChecker;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * A result returned by @{@link ConsistencyChecker#checkConsistency(OWLOntology, DiagnosisModel, OWLReasonerFactory, boolean, boolean, IExquisiteProgressMonitor, ReasonerProgressMonitor)}
 * containing a possibly modified diagnosis model and state about the consistency and coherency of this diagnosis model.
 * Also the axioms added to the correct formulas of the diagnosis model used for coherency check can be retrieved from
 * {@link #getAddedAnonymousIndividuals()}.
 *
 * @author wolfi
 */
public class ConsistencyCheckResult {

    /**
     * Possible return value for {@link #isConsistent()} and {@link #isCoherent()}.
     */
    public final static Boolean UNDEFINED = null;

    /**
     * Information if the diagnosis model (and thus the underlying ontology) is consistent.
     * If nothing is known about the consistency {@link #UNDEFINED} is assigned.
     */
    @Nullable
    private Boolean consistent = UNDEFINED;

    /**
     * Information if the diagnosis model (and thus the underlying ontology) is coherent.
     * If nothing is known about the consistency {@link #UNDEFINED} is assigned.
     */
    @Nullable
    private Boolean coherent = UNDEFINED;

    /**
     * The diagnosis model that is applied during the running debugging session.
     */
    private DiagnosisModel<OWLLogicalAxiom> diagnosisModel;

    /**
     * A subset of anonymous individuals of unsatisfiable classes (e.g. OWLClassAssertionAxioms) in the correct
     * formulas of the diagnosis model. They are added to the diagnosis model's correct axioms for the coherency check
     * when starting a new debugging session.
     */
    private Set<OWLLogicalAxiom> addedAnonymousIndividuals = new HashSet<>();

    @Nullable
    private Set<OWLClass> unsatisfiableClasses;

    @Nullable
    private OWLOntologyManager ontologyManager;

    public void setConsistent(boolean consistent) {
        this.consistent = consistent;
    }

    @Nullable
    public Boolean isConsistent() {
        return consistent;
    }

    public void setDiagnosisModel(DiagnosisModel<OWLLogicalAxiom> diagnosisModel) {
        this.diagnosisModel = diagnosisModel;
    }

    public DiagnosisModel<OWLLogicalAxiom> getDiagnosisModel() {
        return diagnosisModel;
    }

    public void addAnonymousIndividual(OWLLogicalAxiom addedAnonymousIndividual) {
        this.addedAnonymousIndividuals.add(addedAnonymousIndividual);
    }

    public Set<OWLLogicalAxiom> getAddedAnonymousIndividuals() {
        return addedAnonymousIndividuals;
    }

    public void setCoherent(Boolean coherent) {
        this.coherent = coherent;
    }

    @Nullable
    public Boolean isCoherent() {
        return coherent;
    }

    @Nullable
    public Set<OWLClass> getUnsatisfiableClasses() {
        return unsatisfiableClasses;
    }

    public void setUnsatisfiableClasses(@Nullable Set<OWLClass> unsatisfiableClasses) {
        this.unsatisfiableClasses = unsatisfiableClasses;
    }

    public void setOntologyManager(OWLOntologyManager ontologyManager) {
        this.ontologyManager = ontologyManager;
    }

    @Nullable
    public OWLOntologyManager getOntologyManager() {
        return ontologyManager;
    }
}

