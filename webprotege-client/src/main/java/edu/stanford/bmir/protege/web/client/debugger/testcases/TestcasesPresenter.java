package edu.stanford.bmir.protege.web.client.debugger.testcases;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerPresenter;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerResultManager;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallbackWithProgressDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.RemoveTestCaseAction;
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

public class TestcasesPresenter extends DebuggerPresenter {

    @Nonnull
    private TestcasesView view;

    private DispatchServiceManager dsm;

    AcceptsOneWidget container;

    StatementPresenter statementPresenter1;
    StatementPresenter statementPresenter2;

    private final DebuggerResultManager debuggerResultManager;

    DispatchErrorMessageDisplay errorDisplay;

    ProgressDisplay progressDisplay;

    @Nonnull
    private ProjectId projectId;

    @Inject
    public TestcasesPresenter(@Nonnull ProjectId projectId, StatementPresenter statementPresenter1, StatementPresenter statementPresenter2,
                              DispatchServiceManager dispatchServiceManager,
                              MessageBox messageBox, StatementPresenter statementPresenter,
                              DispatchErrorMessageDisplay errorDisplay, ProgressDisplay progressDisplay, DebuggerResultManager debuggerResultManager, TestcasesView view, LoggedInUserProvider loggedInUserProvider) {
        super(statementPresenter, debuggerResultManager,view,loggedInUserProvider);
        this.projectId = projectId;
        this.errorDisplay = errorDisplay;
        this.progressDisplay = progressDisplay;
        this.dsm = dispatchServiceManager;
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
        statementPresenter1.addDeleteTestCasesHandler(this::deleteTestcase);

        statementPresenter2 = new StatementPresenter();
        statementPresenter2.start(view.getNonEntailedcriteriaContainer());
        statementPresenter2.addDeleteTestCasesHandler(this::deleteTestcase);

    }

    public void setAxioms(DebuggingSessionStateResult debuggingSessionStateResult){
        setTestCasesStatement(debuggingSessionStateResult.getPositiveTestCases(),debuggingSessionStateResult.getNegativeTestCases());
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
    public void deleteTestcase(SafeHtml axiom){
        GWT.log("[TestcasesPresenter]Delete Test case Button pressed!!!!!");

        this.dsm.execute(new RemoveTestCaseAction(projectId, axiom),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Remove axioms";
                    }

                    @Override
                    public String getProgressDisplayMessage() {
                        return "Please wait";
                    }

                    public void handleSuccess(DebuggingSessionStateResult debuggingSessionStateResult) {
                        GWT.log("[TestcasesPresenter]debuggingSessionStateResult is "+ debuggingSessionStateResult.getNegativeTestCases());
                        debuggerResultManager.setDebuggingSessionStateResult(debuggingSessionStateResult);
                        debuggerResultManager.updateContent();
                    }
                });
    }


    public void clearAxiomtable() {
        statementPresenter1.clearAxoim();
        statementPresenter2.clearAxoim();
    }
}
