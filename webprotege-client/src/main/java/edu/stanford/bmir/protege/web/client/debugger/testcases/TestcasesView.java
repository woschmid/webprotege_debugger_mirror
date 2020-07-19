package edu.stanford.bmir.protege.web.client.debugger.testcases;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.pagination.HasPagination;
import edu.stanford.bmir.protege.web.shared.entity.EntityNode;
import edu.stanford.bmir.protege.web.shared.pagination.Page;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Jun 2018
 */
public interface TestcasesView extends IsWidget, HasPagination {

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
