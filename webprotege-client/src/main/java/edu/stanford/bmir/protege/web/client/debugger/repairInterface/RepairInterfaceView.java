package edu.stanford.bmir.protege.web.client.debugger.repairInterface;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.Set;

public interface RepairInterfaceView {
    SimplePanel getCriteriaContainer();

    void setAxioms(Set<SafeHtml> axiomStatement);
}
