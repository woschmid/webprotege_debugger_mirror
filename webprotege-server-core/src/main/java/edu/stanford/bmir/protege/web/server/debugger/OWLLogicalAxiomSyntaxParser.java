package edu.stanford.bmir.protege.web.server.debugger;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleIRIShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.HashSet;

/**
 * A parser that converts a string in "inline" Manchester Syntax into an OWLLogicalAxiom object.
 *
 * @see org.obolibrary.macro.ManchesterSyntaxTool
 * @see <a href="https://github.com/owlcs/owlapi/issues/507">https://github.com/owlcs/owlapi/issues/507</a>
 */
public class OWLLogicalAxiomSyntaxParser {

    private static final Logger logger = LoggerFactory.getLogger(OWLLogicalAxiomSyntaxParser.class);

    /**
     * Parses a string represented a logical axiom in Manchester Syntax.
     *
     * @param ontology The ontology the axiom is expected to be expressed in.
     * @param stringToParse The string representation of an logical axiom in Manchester Syntax.
     * @return A logical axiom.
     * @throws OWLParserException Indicates that a parse error happened when trying to parse the string.
     * @throws ClassCastException If the parsing results in an OWLAxiom, but not in an OWLLogicalAxiom.
     */
    public static @Nonnull OWLLogicalAxiom parse(@Nonnull final OWLOntology ontology, @Nonnull final String stringToParse) throws OWLParserException {
        final ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();

        // This class has a dispose method that must be called when not in use anymore!
        // A change listener on the ontology manager is removed then.
        final BidirectionalShortFormProviderAdapter biDirectionalShortFormProvider = new BidirectionalShortFormProviderAdapter(
                ontology.getOWLOntologyManager(),                   // OWLOntologyManager
                new HashSet<>(ontology.getImportsClosure()),        // the ontologies that contain references
                new ShortFormProvider() {                           // The short form provider that should be used to generate the short forms of the referenced entities
                    @Override
                    public void dispose() {}

                    @Override
                    @Nonnull
                    public String getShortForm(@Nonnull OWLEntity entity) {
                        return new SimpleIRIShortFormProvider().getShortForm(entity.getIRI());
                    }
        });

        // ontology to use to resolve classes and entities during parsing
        parser.setDefaultOntology(ontology);
        // an entity checker that maps from string to entities using a bidirectional short form provider.
        parser.setOWLEntityChecker(new ShortFormEntityChecker(biDirectionalShortFormProvider));
        // and finally the string to parse
        parser.setStringToParse(stringToParse);

        try {
            // parsing "Inline" Axioms. The method returns OWLAxioms, but we expect OWLLogicalAxioms
            return (OWLLogicalAxiom) parser.parseAxiom();
        } catch (OWLParserException e) {
            logger.info("Parser exception occurred on string {}", stringToParse);
            logger.info("\tLineNumber: {}", e.getLineNumber());
            logger.info("\tColumnNumber: {}", e.getColumnNumber());
            logger.info("\tMessage:\n{}", e.getMessage());
            throw e;
        } finally {
            // dispose the BidirectionalShortFormProviderAdapter (removes it's change listener from the OWLOntologyManager)
            biDirectionalShortFormProvider.dispose();
        }

    }
}
