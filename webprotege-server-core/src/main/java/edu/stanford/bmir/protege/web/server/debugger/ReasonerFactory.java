package edu.stanford.bmir.protege.web.server.debugger;

import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import javax.annotation.Nonnull;

public class ReasonerFactory {

    public static OWLReasonerFactory getReasonerFactory(@Nonnull final String reasonerId) {
        // List of Reasoners
        // http://owl.cs.manchester.ac.uk/tools/list-of-reasoners/

        // choose between the available reasoners below!
        @Nonnull OWLReasonerFactory reasonerFactory;

        // Select between different popular reasoners here - recompilation necessary!

        switch (reasonerId) {
            case "HermiT": // HermiT Reasoner Factory
                reasonerFactory = new org.semanticweb.HermiT.ReasonerFactory();
                break;
            case "Pellet": // Pellet Reasoner Factory (pellet-owlapi-ignazio1977/2.4.0-ignazio1977)
                reasonerFactory = new com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory();
                break;
            case "JFact": // JFact DL Reasoner Factory (http://jfact.sourceforge.net/)
                reasonerFactory = new uk.ac.manchester.cs.jfact.JFactFactory();
                break;
            case "JCEL": // JCEL OWL API 0.24.1 (https://github.com/julianmendez/jcel)
                reasonerFactory = new de.tudresden.inf.lat.jcel.owlapi.main.JcelReasonerFactory();
                break;
            case "Snorocket": // Snorocket OWL API 4.0.1 (https://github.com/aehrc/snorocket)
                reasonerFactory = new au.csiro.snorocket.owlapi.SnorocketReasonerFactory();
                break;
            case "ELK": // ELK Reasoner 0.4.3 (https://github.com/liveontologies/elk-reasoner)
                reasonerFactory = new org.semanticweb.elk.owlapi.ElkReasonerFactory();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported reasoner with id '" + reasonerId + "'.");
        }

        return reasonerFactory;
    }

}
