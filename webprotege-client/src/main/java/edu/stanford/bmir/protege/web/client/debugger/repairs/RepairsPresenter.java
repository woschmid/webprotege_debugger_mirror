package edu.stanford.bmir.protege.web.client.debugger.repairs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerPresenter;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerResultManager;
import edu.stanford.bmir.protege.web.client.debugger.queries.QueriesPresenter;
import edu.stanford.bmir.protege.web.client.debugger.repairInterface.RepairInterfacePresenter;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallbackWithProgressDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalButtonHandler;
import edu.stanford.bmir.protege.web.client.library.modal.ModalCloser;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.Diagnosis;
import edu.stanford.bmir.protege.web.shared.debugger.RepairAction;
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
    RepairInterfacePresenter repairInterfacePresenter;

    @Nonnull
    private ProjectId projectId;

    @Nonnull
    private final ModalManager modalManager;

    WebProtegeEventBus eventBus;

    interface HandleModalButton extends ModalButtonHandler {
        void handleModalButton(@Nonnull ModalCloser closer);
    }

    @Inject
    public RepairsPresenter(@Nonnull ProjectId projectId,
                            @Nonnull ModalManager modalManager,
                            DispatchServiceManager dsm, @Nonnull RepairInterfacePresenter repairInterfacePresenter,
                            MessageBox messageBox, StatementPresenter statementPresenter,
                            DispatchErrorMessageDisplay errorDisplay, ProgressDisplay progressDisplay, DebuggerResultManager debuggerResultManager, @Nonnull RepairsView view, LoggedInUserProvider loggedInUserProvider) {
        super(statementPresenter, debuggerResultManager,view,loggedInUserProvider,errorDisplay,progressDisplay,messageBox);
        this.view = view;
        this.dsm = dsm;
        this.statementPresenter = statementPresenter;
        this.debuggerResultManager = debuggerResultManager;
        this.modalManager = modalManager;
        this.projectId = projectId;
        this.repairInterfacePresenter = repairInterfacePresenter;
    }


    public void start(AcceptsOneWidget container, WebProtegeEventBus eventBus) {
        super.start(container,eventBus);
        this.eventBus = eventBus;
        statementPresenter.addRepairDebuggingHandler(this::RepairDebugging);
    }

    public void setAxioms(DebuggingSessionStateResult debuggingSessionStateResult){
        statementPresenter.addRepairsStatement(debuggingSessionStateResult.getDiagnoses());
    }

    private void RepairDebugging(Diagnosis diagnosis, int index) {
        GWT.log("[QueriesPresenter]Repair Debugging Button pressed!!!!!");
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle("Repair");
        repairInterfacePresenter.clearAxiomTable();
        repairInterfacePresenter.start(eventBus);
        repairInterfacePresenter.setAxioms(diagnosis);
        modalPresenter.setView(repairInterfacePresenter.getView());
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        HandleModalButton r = (ModalCloser closer) ->
        {
            if(repairInterfacePresenter.getRepairDetails().getAxiomsToModify().isEmpty() && repairInterfacePresenter.getRepairDetails().getAxiomsToDelete().isEmpty()){
                messageBox.showAlert("No Changing", "Please modify or delete at least an axiom.");
            }else{
                GWT.log("[QueriesPresenter]Repair Debugging Button pressed!!!!!" + repairInterfacePresenter.getRepairDetails());
                this.dsm.execute(new RepairAction(projectId, repairInterfacePresenter.getRepairDetails().getAxiomsToModify(), repairInterfacePresenter.getRepairDetails().getAxiomsToDelete(),index),
                        new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
                                progressDisplay) {
                            @Override
                            public String getProgressDisplayTitle() {
                                return "Repairing";
                            }

                            @Override
                            public String getProgressDisplayMessage() {
                                return "Please wait";
                            }

                            public void handleSuccess(DebuggingSessionStateResult debuggingSessionStateResult) {
                                if (debuggingSessionStateResult.isOk()){
                                    closer.closeModal();
                                }
                                handlerDebugging(debuggingSessionStateResult);
                            }
                        });
                repairInterfacePresenter.removeRepairDetails();
            }
        };
        modalPresenter.setPrimaryButton(DialogButton.OK);
        modalPresenter.setButtonHandler(DialogButton.OK, r);
        modalManager.showModal(modalPresenter);
    }


    @Override
    public void setEnabledButton(String buttonTyp) {}


    public void clearAxiomTable() {
        statementPresenter.clearAxoim();
    }

}
