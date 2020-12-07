package edu.stanford.bmir.protege.web.server.debugger;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.IRIShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleIRIShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class OWLLogicalAxiomSyntaxParser {

    public static OWLAxiom parse(OWLOntology ontology, String stringToParse) throws OWLParserException {
        ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
        parser.setDefaultOntology(ontology);
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        // final OWLDataFactory dataFactory = manager.getOWLDataFactory();
        Set<OWLOntology> ontologies = new HashSet<>(ontology.getImportsClosure());

        final IRIShortFormProvider iriShortFormProvider = new SimpleIRIShortFormProvider();

        final ShortFormProvider shortFormProvider = new ShortFormProvider() {

            @Override
            public void dispose() {
                // do nothing
            }

            @Override
            public String getShortForm(@Nonnull OWLEntity entity) {
                return iriShortFormProvider.getShortForm(entity.getIRI());
            }
        };

        OWLEntityChecker owlEntityChecker = new ShortFormEntityChecker(new BidirectionalShortFormProviderAdapter(
                manager, ontologies, shortFormProvider));


        parser.setOWLEntityChecker(owlEntityChecker);
        parser.setStringToParse(stringToParse);
        return parser.parseAxiom();
    }
}
