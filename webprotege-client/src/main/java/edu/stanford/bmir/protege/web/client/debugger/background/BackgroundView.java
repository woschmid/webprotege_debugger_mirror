package edu.stanford.bmir.protege.web.client.debugger.background;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerView;
import edu.stanford.bmir.protege.web.client.pagination.HasPagination;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Jun 2018
 */
public interface BackgroundView extends DebuggerView {

    AcceptsOneWidget getEntailedCriteriaContainer();

    AcceptsOneWidget getNonEntailedcriteriaContainer();

    void setFilterAxiomsHandler(FilterAxiomsHandler filterAxioms);

    void setMoveAllAxiom(MoveAllAxiom moveAllAxiom);

    void setChangePage(ChangePage changePage);

    void setPageCountP(int pageCount);

    void setPageNumberP(int pageNumber);

    int getPageNumberP();

    void setPageNumberChangedHandlerP(HasPagination.PageNumberChangedHandler handler);

    void setPageCountC(int pageCount);

    void setPageNumberC(int pageNumber);

    int getPageNumberC();

    void setPageNumberChangedHandlerC(HasPagination.PageNumberChangedHandler handler);
}
