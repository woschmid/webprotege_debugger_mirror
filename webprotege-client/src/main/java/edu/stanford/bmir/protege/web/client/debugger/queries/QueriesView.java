package edu.stanford.bmir.protege.web.client.debugger.queries;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.progress.HasBusy;
import edu.stanford.bmir.protege.web.shared.entity.EntityNode;
import edu.stanford.bmir.protege.web.shared.individuals.InstanceRetrievalMode;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 12/09/2013
 */
public interface QueriesView extends IsWidget  {

    void setStartDebuggingHandler(@Nonnull StartDebuggingHandler handler);

    void setStopDebuggingHandler(@Nonnull StopDebuggingHandler handler);

    void setSubmitDebuggingHandler(@Nonnull SubmitDebuggingHandler handler);

    void setRepairDebuggingHandler(@Nonnull RepairDebuggingHandler handler);

    AcceptsOneWidget getCriteriaContainer();

    void disablebutton(String s);

    void enablebutton(String s);
}
