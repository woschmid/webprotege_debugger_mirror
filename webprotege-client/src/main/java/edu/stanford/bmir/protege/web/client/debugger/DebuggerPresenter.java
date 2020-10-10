package edu.stanford.bmir.protege.web.client.debugger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.progress.HasBusy;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Matthew Horridge<br> Stanford University<br> Bio-Medical Informatics Research Group<br> Date: 12/09/2013
 */
public abstract class DebuggerPresenter{

    private StatementPresenter statementPresenter;

    private final DebuggerResultManager debuggerResultManager;

    private final DebuggerView view;

    private MessageBox messageBox;

    private final LoggedInUserProvider loggedInUserProvider;

    private HasBusy hasBusy = busy -> {
    };

    public DebuggerPresenter(StatementPresenter statementPresenter,
                             DebuggerResultManager debuggerResultManager,
                             DebuggerView view, LoggedInUserProvider loggedInUserProvider) {

        this.debuggerResultManager = debuggerResultManager;
        this.view = view;
        this.loggedInUserProvider = loggedInUserProvider;
        this.statementPresenter = statementPresenter;
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

    public void clearAxiomtable(){
        statementPresenter.clearAxoim();
    }

    public void setAxoims(DebuggingSessionStateResult debuggingSessionStateResult) {
    }
}
