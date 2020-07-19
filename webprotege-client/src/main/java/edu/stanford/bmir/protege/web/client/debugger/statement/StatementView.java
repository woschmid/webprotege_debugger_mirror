package edu.stanford.bmir.protege.web.client.debugger.statement;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
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
public interface StatementView extends HasSelectionHandlers<List<EntityNode>>, HasBusy, IsWidget {


    void setStatusMessage(String statusMessage);

    void setStatusMessageVisible(boolean visible);

    void setRetrievalMode(@Nonnull InstanceRetrievalMode retrievalType);

    void setRetrievalModeEnabled(boolean enabled);


}
