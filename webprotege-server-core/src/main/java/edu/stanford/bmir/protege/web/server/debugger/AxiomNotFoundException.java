package edu.stanford.bmir.protege.web.server.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;

public class AxiomNotFoundException extends Exception {

    public AxiomNotFoundException(SafeHtml axiom) {
        super("The axiom " + axiom.asString() + " was not be found");
    }
}
