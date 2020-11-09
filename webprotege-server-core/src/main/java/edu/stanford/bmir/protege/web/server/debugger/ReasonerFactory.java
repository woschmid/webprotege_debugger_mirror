package edu.stanford.bmir.protege.web.server.debugger;

import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import javax.annotation.Nonnull;

public class ReasonerFactory {

    public static OWLReasonerFactory getReasonerFactory() {
        // choose between the available reasoners below!
        @Nonnull OWLReasonerFactory reasonerFactory;

        // Select between different popular reasoners here - recompilation necessary!

        // HermiT Reasoner Factory
        // reasonerFactory = new org.semanticweb.HermiT.ReasonerFactory();

        // Pellet Reasoner Factory (pellet-owlapi-ignazio1977/2.4.0-ignazio1977)
        reasonerFactory = new com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory();

        // JFact DL Reasoner Factory (http://jfact.sourceforge.net/)
        // reasonerFactory = new uk.ac.manchester.cs.jfact.JFactFactory();

        return reasonerFactory;
    }
}
