package edu.stanford.bmir.protege.web.client.debugger.queries;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;

import javax.inject.Inject;
//import edu.stanford.bmir.protege.web.client.debuggerPlugin.statement.statementPresenter;

/**
 * Author: Matthew Horridge<br> Stanford University<br> Bio-Medical Informatics Research Group<br> Date: 12/09/2013
 */
public class QueriesPresenter {

    private static final int SEARCH_DELAY = 700;

    private static final int PAGE_SIZE = 200;

    AcceptsOneWidget container;

    StatementPresenter statementPresenter;


    private QueriesViewImpl view;

    @Inject
    public QueriesPresenter(StatementPresenter statementPresenter) {
        this.statementPresenter = statementPresenter;
        this.view = new QueriesViewImpl(this);
    }


    public void start(AcceptsOneWidget container) {
//        GWT.log("Application initialization complete.  Starting UI Initialization.");
//        GWT.log(view.toString());
        this.container = container;
        container.setWidget(view.asWidget());
//        statementPresenter = new StatementPresenter();
        statementPresenter.start(view.getCriteriaContainer(),0);
    }

    protected void addStatement(){
        statementPresenter.addQueriesStatement();
    }


    public void clear() {

    }


}
