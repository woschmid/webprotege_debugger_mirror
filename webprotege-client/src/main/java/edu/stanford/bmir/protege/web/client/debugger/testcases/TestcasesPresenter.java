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
import edu.stanford.bmir.protege.web.client.frame.ManchesterSyntaxFrameEditorPresenter;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalCloser;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.debugger.*;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.stanford.bmir.protege.web.shared.debugger.SessionState.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 12 Jun 2018
 */

public class TestcasesPresenter extends DebuggerPresenter {

    @Nonnull
    ManchesterSyntaxFrameEditorPresenter debuggerManchesterSyntaxFrameEditorEPresenter;

    @Nonnull
    ManchesterSyntaxFrameEditorPresenter debuggerManchesterSyntaxFrameEditorNPresenter;

    @Nonnull
    private TestcasesView view;

    private DispatchServiceManager dsm;

    StatementPresenter statementPresenter1;
    StatementPresenter statementPresenter2;

//    private final DebuggerResultManager debuggerResultManager;

    DispatchErrorMessageDisplay errorDisplay;

    ProgressDisplay progressDisplay;

    @Nonnull
    private final ModalManager modalManager;

    @Nonnull
    private ProjectId projectId;

    @Inject
    public TestcasesPresenter(@Nonnull ProjectId projectId, StatementPresenter statementPresenter1, StatementPresenter statementPresenter2,
                              DispatchServiceManager dispatchServiceManager,
                              MessageBox messageBox, StatementPresenter statementPresenter,
                              DispatchErrorMessageDisplay errorDisplay, ProgressDisplay progressDisplay, DebuggerResultManager debuggerResultManager, TestcasesView view, LoggedInUserProvider loggedInUserProvider, @Nonnull ModalManager modalManager) {
        super(statementPresenter, debuggerResultManager,view,loggedInUserProvider,errorDisplay,progressDisplay,messageBox);
        this.projectId = projectId;
        this.errorDisplay = errorDisplay;
        this.progressDisplay = progressDisplay;
        this.dsm = dispatchServiceManager;
        this.statementPresenter1 = statementPresenter1;
        this.statementPresenter2 = statementPresenter2;
        this.view = view;
        this.modalManager = modalManager;
    }

    WebProtegeEventBus eventBus;
    public void start(AcceptsOneWidget container, WebProtegeEventBus eventBus) {
        container.setWidget(view.asWidget());
        debuggerResultManager.addToList(this);
        this.eventBus = eventBus;

        statementPresenter1 = new StatementPresenter();
        statementPresenter1.start(view.getEntailedCriteriaContainer());
        statementPresenter1.addDeleteTestCasesHandler(this::deleteTestcase);

        statementPresenter2 = new StatementPresenter();
        statementPresenter2.start(view.getNonEntailedcriteriaContainer());
        statementPresenter2.addDeleteTestCasesHandler(this::deleteTestcase);

        this.view.setAddTestcasesEHandler(this::manchesterEditorE);
        this.view.setAddTestcasesNHandler(this::manchesterEditorN);
    }

    public void setAxioms(DebuggingSessionStateResult debuggingSessionStateResult){
        if (debuggingSessionStateResult.getSessionState() == INIT || debuggingSessionStateResult.getSessionState() == CHECKED || debuggingSessionStateResult.getSessionState() == STOPPED ){
            view.enableTestcasesButtons();
        }else{
            view.disableTestcasesButtons();
        }
        setTestCasesStatement(debuggingSessionStateResult.getPositiveTestCases(),debuggingSessionStateResult.getNegativeTestCases());
    }

    @Override
    public void setEnabledButton(String buttonTyp) {

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
                        handlerDebugging(debuggingSessionStateResult);
                    }
                });
    }

    private void manchesterEditorE(boolean clearConcept) {
        if (clearConcept){
            debuggerManchesterSyntaxFrameEditorEPresenter.clearSubject();
        }
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle("Add Entailed Testcase");
        modalPresenter.setView(debuggerManchesterSyntaxFrameEditorEPresenter.getView());
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.addButton(DialogButton.OK);
        modalPresenter.setButtonHandler(DialogButton.OK, this::handleModalButtonE);
        modalManager.showModal(modalPresenter);

    }

    private void handleModalButtonE(ModalCloser closer) {
        GWT.log("[handleModalButton]Get entity: "+ debuggerManchesterSyntaxFrameEditorEPresenter.getView().getValue().get());
        this.dsm.execute(new AddTestCaseAction(projectId,debuggerManchesterSyntaxFrameEditorEPresenter.getView().getValue().get(),true),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Adding positive testcase";
                    }

                    @Override
                    public String getProgressDisplayMessage() {
                        return "Please wait";
                    }

                    public void handleSuccess(DebuggingSessionStateResult debuggingSessionStateResult) {
                        if (!debuggingSessionStateResult.isOk()){
                            manchesterEditorE(false);
                        }
                        handlerDebugging(debuggingSessionStateResult);
                    }
                });
        closer.closeModal();
    }

    private void manchesterEditorN(boolean clearConcept) {
        if (clearConcept){
            debuggerManchesterSyntaxFrameEditorNPresenter.clearSubject();
        }
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle("Add Non-Entailed Testcase");
        modalPresenter.setView(debuggerManchesterSyntaxFrameEditorNPresenter.getView());
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.OK);
        modalPresenter.setButtonHandler(DialogButton.OK,
                this::handleModalButtonN);
        modalManager.showModal(modalPresenter);

    }

    private void handleModalButtonN(ModalCloser closer) {
        GWT.log("[handleModalButton]Get entity: "+ debuggerManchesterSyntaxFrameEditorNPresenter.getView().getValue());
        this.dsm.execute(new AddTestCaseAction(projectId,debuggerManchesterSyntaxFrameEditorNPresenter.getView().getValue().get(),false),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Adding negative testcase";
                    }

                    @Override
                    public String getProgressDisplayMessage() {
                        return "Please wait";
                    }

                    public void handleSuccess(DebuggingSessionStateResult debuggingSessionStateResult) {
                        if (!debuggingSessionStateResult.isOk()){
                            manchesterEditorN(false);
                        }
                        handlerDebugging(debuggingSessionStateResult);
                    }
                });
        closer.closeModal();
    }


    public void clearAxiomTable() {
        statementPresenter1.clearAxoim();
        statementPresenter2.clearAxoim();
    }

    public void setDebuggerManchesterSyntaxFrameEditorEPresenter(ManchesterSyntaxFrameEditorPresenter debuggerManchesterSyntaxFrameEditorEPresenter) {
        this.debuggerManchesterSyntaxFrameEditorEPresenter = debuggerManchesterSyntaxFrameEditorEPresenter;

    }

    public void setDebuggerManchesterSyntaxFrameEditorNPresenter(ManchesterSyntaxFrameEditorPresenter debuggerManchesterSyntaxFrameEditorNPresenter) {
        this.debuggerManchesterSyntaxFrameEditorNPresenter = debuggerManchesterSyntaxFrameEditorNPresenter;

    }
}
