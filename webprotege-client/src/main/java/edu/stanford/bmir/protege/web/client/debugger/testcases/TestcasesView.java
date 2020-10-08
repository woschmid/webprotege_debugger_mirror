package edu.stanford.bmir.protege.web.client.debugger.testcases;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.pagination.HasPagination;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Jun 2018
 */
public interface TestcasesView extends IsWidget, HasPagination {
    public AcceptsOneWidget getEntailedCriteriaContainer();

    public AcceptsOneWidget getNonEntailedcriteriaContainer();

}
