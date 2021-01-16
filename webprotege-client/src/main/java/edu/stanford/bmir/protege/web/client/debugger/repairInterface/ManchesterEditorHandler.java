package edu.stanford.bmir.protege.web.client.debugger.repairInterface;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Button;


public interface ManchesterEditorHandler {
    void addManchesterEditor(SafeHtml selectedAxiom, String axiom, int row,Button m, Button r);
}
