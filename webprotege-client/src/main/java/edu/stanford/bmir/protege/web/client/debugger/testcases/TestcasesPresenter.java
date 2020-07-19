package edu.stanford.bmir.protege.web.client.debugger.testcases;

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

public class TestcasesPresenter {

    @Nonnull
    private final TestcasesViewImpl view;

    AcceptsOneWidget container;

    StatementPresenter statementPresenter1;
    StatementPresenter statementPresenter2;

    @Inject
    public TestcasesPresenter(StatementPresenter statementPresenter1,
                              StatementPresenter statementPresenter2) {
        this.statementPresenter1 = statementPresenter1;
        this.statementPresenter2 = statementPresenter2;
        this.view = new TestcasesViewImpl(this);

    }


    public void start(AcceptsOneWidget container) {

        this.container = container;
        container.setWidget(view.asWidget());
        statementPresenter1 = new StatementPresenter();
        statementPresenter1.start(view.getEntailedCriteriaContainer(),2);

        statementPresenter2 = new StatementPresenter();
        statementPresenter2.start(view.getNonEntailedcriteriaContainer(),2);
    }



}
