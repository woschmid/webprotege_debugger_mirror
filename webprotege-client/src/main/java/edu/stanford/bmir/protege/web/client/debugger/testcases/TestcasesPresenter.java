package edu.stanford.bmir.protege.web.client.debugger.testcases;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerResultManager;
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
    private TestcasesView view;

    AcceptsOneWidget container;

    StatementPresenter statementPresenter1;
    StatementPresenter statementPresenter2;

    private final DebuggerResultManager debuggerResultManager;

    @Inject
    public TestcasesPresenter(StatementPresenter statementPresenter1,
                              StatementPresenter statementPresenter2,
                              TestcasesView testcasesView, DebuggerResultManager debuggerResultManager) {
        this.statementPresenter1 = statementPresenter1;
        this.statementPresenter2 = statementPresenter2;
        this.view = testcasesView;

        this.debuggerResultManager = debuggerResultManager;
    }


    public void start(AcceptsOneWidget container) {

        this.container = container;
        container.setWidget(view.asWidget());
        debuggerResultManager.setTestcasesPresenter(this);
        statementPresenter1 = new StatementPresenter();
        statementPresenter1.start(view.getEntailedCriteriaContainer());

        statementPresenter2 = new StatementPresenter();
        statementPresenter2.start(view.getNonEntailedcriteriaContainer());
    }

    public StatementPresenter getStatementPresenter1() {
        return statementPresenter1;
    }

    public StatementPresenter getStatementPresenter2() {
        return statementPresenter2;
    }

    public void clearAxiomtable() {
        statementPresenter1.clearAxoim();
        statementPresenter2.clearAxoim();
    }
}
