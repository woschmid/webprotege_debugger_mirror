package edu.stanford.bmir.protege.web.client.debugger.repairs;

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
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.DeleteRepairAxiomAction;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 12 Jun 2018
 */

public class RepairsPresenter extends DebuggerPresenter {

    @Nonnull
    private RepairsView view;

    private AcceptsOneWidget container;

    private StatementPresenter statementPresenter;

    private DispatchServiceManager dsm;

    private final DebuggerResultManager debuggerResultManager;

    @Nonnull
    private ProjectId projectId;

    @Nonnull
    private final ModalManager modalManager;

    @Nonnull
    private ManchesterSyntaxFrameEditorPresenter manchesterSyntaxFrameEditorPresenter;


    @Inject
    public RepairsPresenter(@Nonnull ProjectId projectId,
                            DispatchServiceManager dispatchServiceManager,
                            ModalManager modalManager,
                            DispatchServiceManager dsm,
                            ManchesterSyntaxFrameEditorPresenter manchesterSyntaxFrameEditorPresenter,
                            MessageBox messageBox, StatementPresenter statementPresenter,
                            DispatchErrorMessageDisplay errorDisplay, ProgressDisplay progressDisplay, DebuggerResultManager debuggerResultManager, RepairsView view, LoggedInUserProvider loggedInUserProvider) {
        super(statementPresenter, debuggerResultManager,view,loggedInUserProvider,errorDisplay,progressDisplay,messageBox);
        this.view = view;
        this.dsm = dsm;
        this.statementPresenter = statementPresenter;
        this.debuggerResultManager = debuggerResultManager;
        this.manchesterSyntaxFrameEditorPresenter = manchesterSyntaxFrameEditorPresenter;
        this.modalManager = modalManager;
        this.projectId = projectId;
    }


    public void start(AcceptsOneWidget container, WebProtegeEventBus eventBus) {
        super.start(container,eventBus);
//        statementPresenter.addManchesterEditorHandler(this::manchesterEditor);
//        statementPresenter.addDeleteRepairHandler(this::deleteRepairAxiom);
    }

    public void setAxioms(DebuggingSessionStateResult debuggingSessionStateResult){
        statementPresenter.addRepairsStatement(debuggingSessionStateResult.getDiagnoses());
    }

//    private void manchesterEditor() {
//        GWT.log("[RepairInterfacePresenter]+++++++++++++++++++ " + manchesterSyntaxFrameEditorPresenter.getView().getValue());
//        manchesterSyntaxFrameEditorPresenter.getView().setValue("Class: ");
//        ModalPresenter modalPresenter = modalManager.createPresenter();
//        modalPresenter.setTitle("Manchester Editor");
//        modalPresenter.setView(manchesterSyntaxFrameEditorPresenter.getView());
//        modalPresenter.setEscapeButton(DialogButton.CANCEL);
//        modalPresenter.setPrimaryButton(DialogButton.OK);
//        modalPresenter.setButtonHandler(DialogButton.OK,
//                this::handleModalButton);
//        modalManager.showModal(modalPresenter);
//
//    }
//
//    private void deleteRepairAxiom(SafeHtml selectedAxiom){
//        this.dsm.execute(new DeleteRepairAxiomAction(projectId,selectedAxiom),
//                new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
//                        progressDisplay) {
//                    @Override
//                    public String getProgressDisplayTitle() {
//                        return "Repairing";
//                    }
//
//                    @Override
//                    public String getProgressDisplayMessage() {
//                        return "Please wait";
//                    }
//
//                    public void handleSuccess(DebuggingSessionStateResult debuggingSessionStateResult) {
//                        statementPresenter.clearAxoim();
//                        setAxioms(debuggingSessionStateResult);
//                    }
//                });
//    }
//
//    private void handleModalButton(ModalCloser closer) {
//        GWT.log("[handleModalButton]Get entity: "+ manchesterSyntaxFrameEditorPresenter.getView().getValue());
//        closer.closeModal();
//    }

    @Override
    public void setEnabledButton(String buttonTyp) {}

//    public void remove

    public void clearAxiomTable() {
        statementPresenter.clearAxoim();
    }

}
