package edu.stanford.bmir.protege.web.client.debugger.background;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.Configures.ConfigureDebuggerView;
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
                               DispatchErrorMessageDisplay errorDisplay, ProgressDisplay progressDisplay, DebuggerResultManager debuggerResultManager, @Nonnull BackgroundView view, LoggedInUserProvider loggedInUserProvider, @Nonnull ModalManager modalManager, @Nonnull ConfigureDebuggerView configureDebuggerView) {
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

        view.setFilterAxiomsHandler(this::filterAxioms);
        view.setMoveAllAxiom(this::moveAllAxiom);
        view.setChangePage(this::changePageP);
        view.setPageNumberChangedHandlerP(this::changePageP);
        view.setPageNumberChangedHandlerC(this::changePageC);
        view.setSearchStringChangedHandler(this::searchAxioms);
    }

    private void changePageP(int step) {
        GWT.log("[BackgroundPresenter] step is"+ step);
        this.dsm.execute(new PaginationAction( projectId,true,step),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Reading Axioms";
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

    private void changePageC(int step) {
        GWT.log("[BackgroundPresenter] step is"+ step);
        this.dsm.execute(new PaginationAction( projectId,false,step),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Reading Axioms";
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

    private void moveAllAxiom(Boolean down) {
        this.dsm.execute(new MoveAllAction( projectId, down),
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

    public void filterAxioms(boolean Abox, boolean Tbox, boolean Rbox, String text){
        this.dsm.execute(new SetFilterAction( projectId, Abox, Tbox, Rbox, text),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Filter axioms";
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
    boolean isChecked = true;
    public void setAxioms(DebuggingSessionStateResult debuggingSessionStateResult){
        isChecked = debuggingSessionStateResult.getSessionState() != SessionState.STARTED && debuggingSessionStateResult.getSessionState() != SessionState.COMPUTING;
        view.setPageNumberP(debuggingSessionStateResult.getPossiblyFaultyPageIndex());
        view.setPageCountP(debuggingSessionStateResult.getPossiblyFaultyPages());
        view.setPageNumberC(debuggingSessionStateResult.getCorrectPageIndex());
        view.setPageCountC(debuggingSessionStateResult.getCorrectPages());
        view.setPFANumber(debuggingSessionStateResult.getNrPossiblyFaultyAxioms());
        view.setCANumber(debuggingSessionStateResult.getNrCorrectAxioms());
        view.deActiveButton(isChecked);
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

    public void searchAxioms(){
        GWT.log("[BackgroundPresenter] Search keys are: "+ view.getSearchString());
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
