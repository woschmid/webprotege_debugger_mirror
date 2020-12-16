package edu.stanford.bmir.protege.web.client.debugger.background;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.ConfigureDebuggerView;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerPresenter;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerResultManager;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallbackWithProgressDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.debugger.*;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 12 Jun 2018
 */

public class BackgroundPresenter extends DebuggerPresenter {

    @Nonnull
    private BackgroundView view;

    private DispatchServiceManager dsm;

    StatementPresenter statementPresenter1;
    StatementPresenter statementPresenter2;

    private final DebuggerResultManager debuggerResultManager;

    @Nonnull
    private ProjectId projectId;

    @Inject
    public BackgroundPresenter(@Nonnull ProjectId projectId,
                               StatementPresenter statementPresenter1, StatementPresenter statementPresenter2,
                               DispatchServiceManager dispatchServiceManager,
                               MessageBox messageBox, StatementPresenter statementPresenter,
                               DispatchErrorMessageDisplay errorDisplay, ProgressDisplay progressDisplay, DebuggerResultManager debuggerResultManager, BackgroundView view, LoggedInUserProvider loggedInUserProvider, @Nonnull ModalManager modalManager, @Nonnull ConfigureDebuggerView configureDebuggerView) {
        super(statementPresenter, debuggerResultManager,view,loggedInUserProvider,errorDisplay,progressDisplay,messageBox);
        this.projectId = projectId;
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

        statementPresenter2 = new StatementPresenter();
        statementPresenter2.start(view.getNonEntailedcriteriaContainer());

        statementPresenter1.addFaultyAxiomRemoveHandler(this::handlerReplaceAxiom);
        statementPresenter2.addBackgroundAxiomRemoveHandler(this::handlerReplaceAxiom);

    }
    boolean isChecked = true;
    public void setAxioms(DebuggingSessionStateResult debuggingSessionStateResult){

        if(debuggingSessionStateResult.getSessionState() == SessionState.STARTED || debuggingSessionStateResult.getSessionState() == SessionState.COMPUTING ) {
            isChecked = false;
        } else{
            isChecked = true;
        }
        setPossibleFaultyAxioms(debuggingSessionStateResult.getPossiblyFaultyAxioms());
        setBackgroundAxioms(debuggingSessionStateResult.getCorrectAxioms());
    }

    @Override
    public void setEnabledButton(String buttonTyp) {

    }

    List<SafeHtml> backgroundAxioms = new ArrayList<>();
    List<SafeHtml> possibleFaultyAxioms = new ArrayList<>();

    public void setPossibleFaultyAxioms(PossiblyFaultyAxioms axioms){
        if (axioms != null) {
            List<SafeHtml> items = axioms.getAxioms();
            possibleFaultyAxioms.addAll(items);
            setAxiomsToViews();
        }
    }

    public void setBackgroundAxioms(CorrectAxioms axioms){
        if (axioms != null) {
            List<SafeHtml> items = axioms.getAxioms();
            backgroundAxioms.addAll(items);
            setAxiomsToViews();
        }
    }

    private void setAxiomsToViews() {
        statementPresenter1.addPossibleFaultyAxioms( new ArrayList<>(),possibleFaultyAxioms, isChecked);
        statementPresenter2.addPossibleFaultyAxioms( backgroundAxioms, new ArrayList<>(), isChecked);
    }

    public void handlerReplaceAxiom(SafeHtml axiom){
        GWT.log("[BackgroundPresenter]Replace Button pressed!!!!!");

        this.dsm.execute(new MoveToAction(projectId, axiom),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Move axioms";
                    }

                    @Override
                    public String getProgressDisplayMessage() {
                        return "Please wait";
                    }

                    public void handleSuccess(DebuggingSessionStateResult debuggingSessionStateResult) {
                        handlerDebugging(debuggingSessionStateResult);
                    }
                });
    }

    public void clearAxiomTable() {
        backgroundAxioms.clear();
        possibleFaultyAxioms.clear();
        statementPresenter1.clearAxoim();
        statementPresenter2.clearAxoim();
    }
}
