package edu.stanford.bmir.protege.web.client.debugger.background;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerPresenter;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerResultManager;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.TestCase;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 12 Jun 2018
 */

public class BackgroundPresnter extends DebuggerPresenter {

    @Nonnull
    private BackgroundView view;

    AcceptsOneWidget container;

    StatementPresenter statementPresenter1;
    StatementPresenter statementPresenter2;

    private final DebuggerResultManager debuggerResultManager;

    @Inject
    public BackgroundPresnter(@Nonnull ProjectId projectId, StatementPresenter statementPresenter1, StatementPresenter statementPresenter2,
                              DispatchServiceManager dispatchServiceManager,
                              MessageBox messageBox, StatementPresenter statementPresenter,
                              DispatchErrorMessageDisplay errorDisplay, ProgressDisplay progressDisplay, DebuggerResultManager debuggerResultManager, BackgroundView view, LoggedInUserProvider loggedInUserProvider) {
        super(statementPresenter, debuggerResultManager,view,loggedInUserProvider);
        this.statementPresenter1 = statementPresenter1;
        this.statementPresenter2 = statementPresenter2;
        this.view = view;

        this.debuggerResultManager = debuggerResultManager;
    }


    public void start(AcceptsOneWidget container) {
        container.setWidget(view.asWidget());
        debuggerResultManager.addToList(this);

        statementPresenter1 = new StatementPresenter();
        statementPresenter1.start(view.getEntailedCriteriaContainer());

        statementPresenter2 = new StatementPresenter();
        statementPresenter2.start(view.getNonEntailedcriteriaContainer());

    }

    public void setAxoims(DebuggingSessionStateResult debuggingSessionStateResult){
//        setTestCasesStatement(debuggingSessionStateResult.getPositiveTestCases(),debuggingSessionStateResult.getNegativeTestCases());
    }

    public void setTestCasesStatement(List<TestCase> msgP, List<TestCase> msgN){
        Set<SafeHtml> itemsP = new HashSet<>();
        if (msgP != null) {
            for (TestCase p : msgP) itemsP.add(p.getAxiom());
        }

        Set<SafeHtml> itemsN = new HashSet<>();
        if (msgN != null) {
            for (TestCase n : msgN) itemsN.add(n.getAxiom());
        }

        statementPresenter1.addTestCasesStatement(itemsP);
        statementPresenter2.addTestCasesStatement(itemsN);
    }

    public void clearAxiomtable() {
        statementPresenter1.clearAxoim();
        statementPresenter2.clearAxoim();
    }
}
