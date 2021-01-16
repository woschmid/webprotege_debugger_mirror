package edu.stanford.bmir.protege.web.client.debugger.repairs;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerView;
import edu.stanford.bmir.protege.web.shared.entity.EntityNode;
import edu.stanford.bmir.protege.web.shared.pagination.Page;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Jun 2018
 */
public interface RepairsView extends DebuggerView {


    interface ExecuteQueryHandler {
        void handleExecute();
    }

    void setExecuteHandler(@Nonnull ExecuteQueryHandler handler);

    void setExecuteEnabled(boolean enabled);

    @Nonnull
    AcceptsOneWidget getCriteriaContainer();

    void clearResults();

    void setResult(@Nonnull Page<EntityNode> result);
}
