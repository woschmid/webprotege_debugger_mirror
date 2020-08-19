package edu.stanford.bmir.protege.web.client.debugger.repairs;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.RootPanel;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;


import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 12 Jun 2018
 */

public class RepairsPresenter {

    @Nonnull
    private final RepairsViewImpl view;

    AcceptsOneWidget container;

    StatementPresenter statementPresenter;


    @Inject
    public RepairsPresenter(StatementPresenter statementPresenter) {
        this.statementPresenter = statementPresenter;
        this.view = new RepairsViewImpl(this);
    }


    public void start(AcceptsOneWidget container) {
//        GWT.log("Application initialization complete.  Starting UI Initialization.");
//        GWT.log(view.toString());
        this.container = container;
        container.setWidget(view.asWidget());
        statementPresenter.start(view.getCriteriaContainer());
    }


    public StatementPresenter getStatementPresenter() {
        return statementPresenter;
    }

    public void setStatementPresenter(StatementPresenter statementPresenter) {
        this.statementPresenter = statementPresenter;
    }

    public void clearAxiomtable() {
        statementPresenter.clearAxoim();
    }
}
