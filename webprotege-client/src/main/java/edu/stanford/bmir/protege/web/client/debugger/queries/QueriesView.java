package edu.stanford.bmir.protege.web.client.debugger.queries;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerView;

import javax.annotation.Nonnull;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 12/09/2013
 */
public interface QueriesView extends DebuggerView {

    void setStartDebuggingHandler(@Nonnull StartDebuggingHandler handler);

    void setStopDebuggingHandler(@Nonnull StopDebuggingHandler handler);

    void setSubmitDebuggingHandler(@Nonnull SubmitDebuggingHandler handler);

    void setRepairDebuggingHandler(@Nonnull RepairDebuggingHandler handler);

    void setCheckOntologyHandler(@Nonnull CheckOntologyHandler handler);

    void changeStartButton(@Nonnull boolean start);

    AcceptsOneWidget getCriteriaContainer();

    void disablebutton(String s);

    void enablebutton(String s);
}
