package edu.stanford.bmir.protege.web.client.debugger;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 12/09/2013
 */
public interface DebuggerView extends IsWidget {
    public AcceptsOneWidget getCriteriaContainer();
}
