package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.shared.debugger.Preferences;
import org.exquisite.core.DiagnosisRuntimeException;
import org.exquisite.core.IExquisiteProgressMonitor;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.InconsistentDiagnosisModelException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.OntologyCopy;
import org.semanticweb.owlapi.reasoner.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class ConsistencyChecker {

    private static final Logger logger = LoggerFactory.getLogger(ConsistencyChecker.class);

    @Nonnull
    public static ConsistencyCheckResult checkConsistencyAndCoherency(@Nonnull DebuggingSession debuggingSession,
                                                                      @Nonnull OWLOntology ontology,
                                                                      @Nonnull DiagnosisModel<OWLLogicalAxiom> dm,
                                                                      @Nonnull OWLReasonerFactory reasonerFactory,
                                                                      @Nonnull ReasonerProgressMonitor reasonerProgressMonitor,
                                                                      @Nonnull Preferences preferences) throws OWLOntologyCreationException {

        OWLReasoner reasoner = null; // this is a temporary reasoner to be used for consistency and coherency checks
        ConsistencyCheckResult result = new ConsistencyCheckResult();
        try {
            // start a new progress monitor task for consistency/coherency check
            logger.info("{} " + IExquisiteProgressMonitor.CONSISTENCY_COHERENCY_CHECK + " using " + ((reasonerFactory.getReasonerName() != null) ? reasonerFactory.getReasonerName() : "HermiT"), debuggingSession);

            final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
            result.setOntologyManager(ontologyManager);

            OWLOntology ontologyCopy = ontologyManager.copyOntology(ontology, OntologyCopy.DEEP); // (1) ontology already does contain the correct
            List<OWLLogicalAxiom> addedEntailedAxioms = addAxiomsToOntology(dm.getEntailedExamples(), ontologyCopy);

            reasoner = createReasoner(ontologyCopy, reasonerFactory, reasonerProgressMonitor, preferences);
            logger.info("{} Solver created: {}", debuggingSession, reasoner);

            Set<OWLLogicalAxiom> possiblyFaulty = new TreeSet<>();

            // in case the ontology is consistent we assume that the user wants to debug the incoherency.
            logger.info("{} Checking the consistency of the ontology ...", debuggingSession);

            if (reasoner.isConsistent()) {
                result.setConsistent(true);

                logger.info("{} ... the ontology is consistent!", debuggingSession);

                // we check for coherency

                logger.info("{} Checking the coherency of the ontology ...", debuggingSession);
                logger.info("{} ... computing unsatisfiable classes ...", debuggingSession);

                reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
                final Set<OWLClass> unsatisfiableClasses = reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom();

                if (!unsatisfiableClasses.isEmpty()) { // we have an incoherent ontology

                    result.setUnsatisfiableClasses(unsatisfiableClasses);
                    result.setCoherent(false);

                    logger.info("{} ... found " + unsatisfiableClasses.size() + " unsatisfiable classes ...", debuggingSession);
                    logger.info("{} ... the ontology is incoherent!", debuggingSession);

                    //if (isModuleExtractionEnabled) {
                    // Module Extraction

                    SyntacticLocalityModuleExtractor extractor = new SyntacticLocalityModuleExtractor(ontologyManager,
                            ontologyCopy, ModuleType.STAR);

                    Set<OWLEntity> entities = unsatisfiableClasses.stream()
                            .map(o -> (OWLEntity) o).collect(Collectors.toSet());

                    // Fix for Issue #135: take into account manually added negative test cases
                    for (OWLLogicalAxiom negativeTestcase : dm.getNotEntailedExamples()) {
                        Set<OWLEntity> fEntities = negativeTestcase.getClassesInSignature().stream()
                                .map(o -> (OWLEntity) o).collect(Collectors.toSet());
                        entities.addAll(fEntities);
                    }

                    logger.info("{} Applying module extraction based on the seed signature of " + entities.size() + " entities ...", debuggingSession);

                    possiblyFaulty = extractor.extract(entities).stream().filter(OWLLogicalAxiom.class::isInstance).
                            map(o -> (OWLLogicalAxiom) o).collect(Collectors.toSet());

                    logger.info("{} ... leads to a reduction to " + possiblyFaulty.size() + " possibly faulty axioms.", debuggingSession);

                    // } else { // no module extraction or no unsatisfiable classes
                    //     possiblyFaulty.addAll(ontologyCopy.getLogicalAxioms());
                    // }

                    // no reduction to inconsistency!
                    // reduceToInconsistency(dm, monitor, result, ontologyManager, unsatisfiableClasses);

                } else { // no unsatisfiable classes therefore coherent ontology
                    result.setCoherent(true);
                    logger.info("{} ... no unsatisfiable classes found ...", debuggingSession);
                    logger.info("{} ... the ontology is coherent!", debuggingSession);

                    possiblyFaulty.addAll(ontologyCopy.getLogicalAxioms());
                } // end of incoherency check


            } else { // inconsistent ontology - no check for incoherency (thus, incoherency remains UNDEFINED in result!)
                result.setConsistent(false);
                possiblyFaulty.addAll(ontologyCopy.getLogicalAxioms());
            }

            // make sure that all sets are disjoint
            possiblyFaulty.removeAll(dm.getCorrectFormulas());

            // remove the added entailed axioms (4)
            possiblyFaulty.removeAll(addedEntailedAxioms);

            dm.setPossiblyFaultyFormulas(possiblyFaulty);

            result.setDiagnosisModel(dm);
            return result;
        } finally {
            // In the advent of some exception (which might occur, depending on the reasoners and their support
            // of the given ontology, in any case we stop all tasks.
            reasonerProgressMonitor.reasonerTaskStopped();
            if (reasoner != null) {
                reasoner.dispose(); // call this after the monitors
                logger.info("{} Solver disposed: {}", debuggingSession, reasoner);
            }
        }

    }

    public static void reduceToInconsistency(DiagnosisModel<OWLLogicalAxiom> dm, IExquisiteProgressMonitor monitor, ConsistencyCheckResult result) {
        // instantiate unsatisfiable classes and thus reducing the incoherency to inconsistency

        final OWLOntologyManager ontologyManager = result.getOntologyManager();
        final Set<OWLClass> unsatisfiableClasses = result.getUnsatisfiableClasses();

        if (ontologyManager == null ||unsatisfiableClasses == null)
            throw new RuntimeException("Unexpected null value for ontology manager and/or unsatisfiableClasses during reduction to inconsistency");

        if (monitor != null) monitor.taskBusy("Reducing incoherency to inconsistency ... ");

        Set<OWLClass> unsatisfiableClassesInInconsistentCorrectFormulas = new HashSet<>();

        for (OWLClass unsatisfiableClass : unsatisfiableClasses) {
            try {
                OWLClassAssertionAxiom addedAnonymousIndividual = instantiateUnsatisfiableClass(unsatisfiableClass, dm.getCorrectFormulas(), ontologyManager.getOWLDataFactory());
                result.addAnonymousIndividual(addedAnonymousIndividual);
            } catch (DiagnosisRuntimeException e) {
                // an inconsistency occurred among the correct formulas, remember the involved unsat class
                unsatisfiableClassesInInconsistentCorrectFormulas.add(unsatisfiableClass);
            }
        }

        // Remove the previously added anonymous individuals if an inconsistency exists between the correct formulas
        if (!unsatisfiableClassesInInconsistentCorrectFormulas.isEmpty()) {
            dm.getCorrectFormulas().removeAll(result.getAddedAnonymousIndividuals());
            throw new InconsistentDiagnosisModelException(unsatisfiableClassesInInconsistentCorrectFormulas);
        }

        if (monitor != null && !result.getAddedAnonymousIndividuals().isEmpty())
            monitor.taskBusy("... leads to the addition of " + result.getAddedAnonymousIndividuals().size() + " anonymous individuals to the background");
    }

    /**
     * Adds an axiom from axioms to the ontology only if it is not yet defined in the ontology.
     *
     * @param axioms   The axiom candidates to be added.
     * @param ontology The ontology.
     * @return The list of added axioms.
     */
    @Nonnull
    private static List<OWLLogicalAxiom> addAxiomsToOntology(List<OWLLogicalAxiom> axioms, OWLOntology ontology) {
        List<OWLLogicalAxiom> addedAxioms = new ArrayList<>();
        if (!axioms.isEmpty()) {
            List<OWLAxiomChange> changes = new ArrayList<>(axioms.size());
            for (OWLLogicalAxiom axiom : axioms) {
                if (!ontology.containsAxiom(axiom)) {
                    changes.add(new AddAxiom(ontology, axiom));
                    addedAxioms.add(axiom);
                }
            }
            if (!changes.isEmpty()) {
                ontology.getOWLOntologyManager().applyChanges(changes);
            }
        }
        return addedAxioms;
    }

    private static OWLReasoner createReasoner(OWLOntology ontology, OWLReasonerFactory factory, ReasonerProgressMonitor monitor, Preferences preferences) {
        OWLReasonerConfiguration configuration = new SimpleConfiguration(monitor, preferences.getReasonerTimeoutInMillis());
        return factory.createReasoner(ontology, configuration);
    }

    /**
     * Creates a ClassAssertionAxiom for an anonymous individual of an unsatisfiable class and assigns it to the correct
     * formulas and thus reducing incoherency to inconsistency. Additionally the new ClassAssertionAxiom is returned.
     *
     * @param unsatisfiableClass An unsatisfiable class.
     * @param correctFormulas    The set of correct axioms of a diagnosis model.
     * @param df                 A datafactory.
     * @return The new ClassAssertionAxiom or <code>null</code> if no creation was necessary.
     * @throws DiagnosisRuntimeException if adding the new axiom leads to an inconsistency among the correct formulas.
     *                                   The axiom is removed then.
     */
    private static OWLClassAssertionAxiom instantiateUnsatisfiableClass(OWLClass unsatisfiableClass, List<OWLLogicalAxiom> correctFormulas, OWLDataFactory df) {
        final OWLClassAssertionAxiom axiom = df.getOWLClassAssertionAxiom(unsatisfiableClass, df.getOWLAnonymousIndividual());
        try {
            correctFormulas.add(axiom);
        } catch (DiagnosisRuntimeException e) {
            correctFormulas.remove(axiom);
            throw new DiagnosisRuntimeException();
        }
        return axiom;
    }
}
