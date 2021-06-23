package edu.stanford.bmir.protege.web.server.shacl;

import edu.stanford.bmir.protege.web.server.debugger.ReasonerFactory;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.debugger.DefaultValues;
import edu.stanford.bmir.protege.web.shared.dispatch.ActionExecutionException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.TimedConsoleProgressMonitor;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

import java.util.ArrayList;
import java.util.List;

import static edu.stanford.bmir.protege.web.shared.debugger.DefaultValues.REASONER_TIMEOUT_IN_MILLIS;

public class InferredOntologyLoader {

    public static OWLOntology loadInferredOntology(final RevisionManager revisionManager)
            throws OWLOntologyCreationException {
        OWLOntology ont = loadOntology(revisionManager);
        OWLReasoner reasoner = createReasoner(ont);
        return loadInferredOntologyFromReasoner(ont, reasoner);
    }

    public static OWLOntology loadInferredOntologyFromReasoner(OWLOntology ont, OWLReasoner reasoner)
            throws OWLOntologyCreationException {
        // NOTE: we cannot use modelManager.getOWLOntologyManager()
        // as this would mess up the loaded ontologies in Protégé
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory fac = manager.getOWLDataFactory();

        // copy all axioms from the ontology and include all axioms from all imported ontologies
        OWLOntology infOnt = manager.createOntology(ont.getAxioms(Imports.INCLUDED));

        // copy all axioms that the reasoner inferred
        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner);
        iog.fillOntology(fac, infOnt);

        return infOnt;
    }

    /**
     * Loads the ontology and generates a diagnosis model from this ontology.
     */
    private static OWLOntology loadOntology(final RevisionManager revisionManager) {
        // loading ontology and generating diagnosis model
        final OWLOntologyManager ontologyManager = revisionManager.getOntologyManagerForRevision(revisionManager.getCurrentRevision());
        final List<OWLOntology> ontologies = new ArrayList<>(ontologyManager.getOntologies());
        if (ontologies.size() != 1)
            throw new ActionExecutionException(new RuntimeException("Expected only one ontology but there are " + ontologies.size()));

        return ontologies.get(0);
    }

    private static OWLReasoner createReasoner(final OWLOntology ontology) {
        final OWLReasonerConfiguration configuration = new SimpleConfiguration(new TimedConsoleProgressMonitor(), REASONER_TIMEOUT_IN_MILLIS);
        return ReasonerFactory.getReasonerFactory(DefaultValues.reasonerId).createReasoner(ontology, configuration);
    }
}
