package edu.stanford.bmir.protege.web.server.debugger;

import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * A renderer util to transform OWLLogicalAxiom to their compact string representations.
 */
public class ManchesterSyntaxRenderer {

    private static ManchesterOWLSyntaxOWLObjectRendererImpl renderer = new ManchesterOWLSyntaxOWLObjectRendererImpl();

    @Nonnull
    public static String renderAxiom(@Nonnull OWLLogicalAxiom axiom) {
        return renderer.render(axiom);
    }

    @Nonnull
    public static Set<String> renderAxioms(@Nonnull Set<OWLLogicalAxiom> axioms) {
        Set<String> set = new HashSet<>();
        for (OWLLogicalAxiom axiom : axioms)
            set.add(renderAxiom(axiom));
        return set;
    }
}
