package edu.stanford.bmir.protege.web.client.debugger.testcases;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerView;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Jun 2018
 */
public interface TestcasesView extends DebuggerView {

    public AcceptsOneWidget getEntailedCriteriaContainer();

    public AcceptsOneWidget getNonEntailedcriteriaContainer();

}
