package edu.stanford.bmir.protege.web.client.debugger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.progress.HasBusy;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.SessionState;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Matthew Horridge<br> Stanford University<br> Bio-Medical Informatics Research Group<br> Date: 12/09/2013
 */
public abstract class DebuggerPresenter{

    protected StatementPresenter statementPresenter;

    protected final DebuggerResultManager debuggerResultManager;

    private final DebuggerView view;

    protected final LoggedInUserProvider loggedInUserProvider;

    protected DispatchErrorMessageDisplay errorDisplay;

    protected ProgressDisplay progressDisplay;


    protected MessageBox messageBox;

    private HasBusy hasBusy = busy -> {
    };

    public DebuggerPresenter(StatementPresenter statementPresenter,
                             DebuggerResultManager debuggerResultManager,
                             DebuggerView view, LoggedInUserProvider loggedInUserProvider,
                             DispatchErrorMessageDisplay errorDisplay, ProgressDisplay progressDisplay,MessageBox messageBox ) {

        this.debuggerResultManager = debuggerResultManager;
        this.view = view;
        this.loggedInUserProvider = loggedInUserProvider;
        this.statementPresenter = statementPresenter;
        this.errorDisplay = errorDisplay;
        this.progressDisplay = progressDisplay;
        this.messageBox = messageBox;
        GWT.log("[DebuggerPresenter] Started DebuggerPresenter");
    }

    public void setHasBusy(@Nonnull HasBusy hasBusy) {
        this.hasBusy = checkNotNull(hasBusy);
    }


    public void start(AcceptsOneWidget container, WebProtegeEventBus eventBus) {
        container.setWidget(view.asWidget());
        debuggerResultManager.addToList(this);
        statementPresenter.start(view.getCriteriaContainer());
    }

    public abstract void clearAxiomTable();

    public abstract void setAxioms(DebuggingSessionStateResult debuggingSessionStateResult);

    protected void handlerDebugging(DebuggingSessionStateResult debuggingSessionStateResult) {
        boolean isStop = false;
        if (debuggingSessionStateResult.getSessionState() == SessionState.STOPPED) {
            isStop = true;
        }
        if (!debuggingSessionStateResult.isOk()) {
            messageBox.showAlert("Error", debuggingSessionStateResult.getMessage());
            setEnabledButton("locked");
        } else {
            if (debuggingSessionStateResult.getMessage() != null) {
                messageBox.showAlert("Information", debuggingSessionStateResult.getMessage());
            }
            GWT.log("[QueriesPresenter]debuggingSessionStateResult is "+ debuggingSessionStateResult);
            debuggerResultManager.setDebuggingSessionStateResult(debuggingSessionStateResult);
            showResults(debuggingSessionStateResult, isStop);
            checkFinal(debuggingSessionStateResult);
        }

    }

    protected void showResults(DebuggingSessionStateResult debuggingSessionStateResult, boolean isStop) {
        if(!isStop){
            debuggerResultManager.updateContent();
        }
        debuggerResultManager.changeButtonStatus();
    }

    protected void checkFinal(DebuggingSessionStateResult result){
        if (result.getSessionState() == SessionState.STARTED &&
                (result.getQuery() == null && result.getDiagnoses() != null &&
                        result.getDiagnoses().size() == 1)){
            setEnabledButton("repair");
        }else{
            debuggerResultManager.changeButtonStatus();
        }

    }
    public abstract void setEnabledButton(String buttonTyp);
}
