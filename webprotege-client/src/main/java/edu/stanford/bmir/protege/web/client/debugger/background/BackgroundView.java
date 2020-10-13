package edu.stanford.bmir.protege.web.client.debugger.background;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerView;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Jun 2018
 */
public interface BackgroundView extends DebuggerView {

    public AcceptsOneWidget getEntailedCriteriaContainer();

    public AcceptsOneWidget getNonEntailedcriteriaContainer();

}
