package edu.stanford.bmir.protege.web.server.debugger;

import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import javax.annotation.Nonnull;

public class ReasonerFactory {

    public static OWLReasonerFactory getReasonerFactory() {
        // List of Reasoners
        // http://owl.cs.manchester.ac.uk/tools/list-of-reasoners/

        // choose between the available reasoners below!
        @Nonnull OWLReasonerFactory reasonerFactory;

        // Select between different popular reasoners here - recompilation necessary!

        // HermiT Reasoner Factory
        reasonerFactory = new org.semanticweb.HermiT.ReasonerFactory();

        // Pellet Reasoner Factory (pellet-owlapi-ignazio1977/2.4.0-ignazio1977)
        // reasonerFactory = new com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory();

        // JFact DL Reasoner Factory (http://jfact.sourceforge.net/)
        // reasonerFactory = new uk.ac.manchester.cs.jfact.JFactFactory();

        // JCEL OWL API 0.24.1 (https://github.com/julianmendez/jcel)
        // reasonerFactory = new de.tudresden.inf.lat.jcel.owlapi.main.JcelReasonerFactory();

        // Snorocket OWL API 4.0.1 (https://github.com/aehrc/snorocket)
        // reasonerFactory = new au.csiro.snorocket.owlapi.SnorocketReasonerFactory();

        // ELK Reasoner 0.4.3 (https://github.com/liveontologies/elk-reasoner)
        // reasonerFactory = new org.semanticweb.elk.owlapi.ElkReasonerFactory();

        return reasonerFactory;
    }
}
