package edu.stanford.bmir.protege.web.client.debugger.repairInterface;



import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Button;
import edu.stanford.bmir.protege.web.client.debugger.AutoConfigAxiom;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerPresenter;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerResultManager;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallbackWithProgressDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.frame.ManchesterSyntaxFrameEditorImpl;
import edu.stanford.bmir.protege.web.client.frame.ManchesterSyntaxFrameEditorPresenter;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalButtonHandler;
import edu.stanford.bmir.protege.web.client.library.modal.ModalCloser;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.DirtyChangedEvent;
import edu.stanford.bmir.protege.web.shared.debugger.*;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RepairInterfacePresenter extends DebuggerPresenter {
    @Nonnull
    private final ModalManager modalManager;

    private DispatchServiceManager dsm;

    private LoggedInUserProvider loggedInUserProvider;


    private DispatchErrorMessageDisplay errorDisplay;


    private ProgressDisplay progressDisplay;

    private Set<SafeHtml> axiomsToDelete = new HashSet<>();

    private Map<SafeHtml, String> axiomsToModify = new HashMap<>();

    interface HandleModalButton extends ModalButtonHandler{
        void handleModalButton(@Nonnull ModalCloser closer);
    }

    @Nonnull
    private ProjectId projectId;


    private RepairInterfaceViewImpl view;

    @Nonnull
    ManchesterSyntaxFrameEditorImpl manchesterSyntaxFrameEditor;

    @Inject
    public RepairInterfacePresenter(@Nonnull ModalManager modalManager, @Nonnull DispatchServiceManager dsm,
                                    @Nonnull LoggedInUserProvider loggedInUserProvider,
                                    @Nonnull DispatchErrorMessageDisplay errorDisplay,
                                    @Nonnull ProgressDisplay progressDisplay,
                                    @Nonnull ProjectId projectId,
                                    MessageBox messageBox, StatementPresenter statementPresenter,
                                    RepairInterfaceViewImpl view, DebuggerResultManager debuggerResultManager,
                                    @Nonnull ManchesterSyntaxFrameEditorImpl manchesterSyntaxFrameEditor) {
        super(statementPresenter, debuggerResultManager,view,loggedInUserProvider,errorDisplay,progressDisplay,messageBox);
        this.errorDisplay =  errorDisplay;
        this.progressDisplay = progressDisplay;
        this.dsm = dsm;
        this.loggedInUserProvider = loggedInUserProvider;
        this.projectId = projectId;
        this.modalManager = modalManager;
        this.view = view;
        this.manchesterSyntaxFrameEditor = manchesterSyntaxFrameEditor;
    }

    public void start(WebProtegeEventBus eventBus) {
        view.setManchesterEditorHandler(this::manchesterEditor);
        view.setDeleteRepairHandler(this::deleteRepairAxiom);
        view.setRedoRepairHandler(this::redoRepair);
    }

    public void setAxioms(Diagnosis diagnosis){
        view.setAxioms(diagnosis.getAxioms());
    }

    private void manchesterEditor(SafeHtml selectedAxiom, String axiom, int row,Button buttonM, Button buttonR) {
        AutoConfigAxiom autoConfigAxiom= new AutoConfigAxiom(axiom);
        axiom = autoConfigAxiom.fixAxiom();
        manchesterSyntaxFrameEditor.clearValue();
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalManager.showModal(modalPresenter);
        modalPresenter.setTitle("Manchester Editor");
        modalPresenter.setView(manchesterSyntaxFrameEditor.asWidget());
        manchesterSyntaxFrameEditor.setValue(axiom);
        GWT.log("[handleModalButton]Get entity: "+ manchesterSyntaxFrameEditor.getValue());
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        String finalAxiom = axiom;
        HandleModalButton r = (ModalCloser closer) ->
        {
            if (manchesterSyntaxFrameEditor.getValue().isPresent()){
                String changedAxiom = manchesterSyntaxFrameEditor.getValue().get();
                if (!finalAxiom.equals(changedAxiom)){
                    this.dsm.execute(new CheckAxiomSyntaxAction(projectId, changedAxiom),
                            new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
                                    progressDisplay) {
                                @Override
                                public String getProgressDisplayTitle() {
                                    return "Checking Syntax";
                                }

                                @Override
                                public String getProgressDisplayMessage() {
                                    return "Please wait";
                                }

                                public void handleSuccess(DebuggingSessionStateResult debuggingSessionStateResult) {
                                    handlerDebugging(debuggingSessionStateResult);
                                    if(debuggingSessionStateResult.isOk() ){
                                        axiomsToModify.put(selectedAxiom,changedAxiom);
                                        view.changAxoim(changedAxiom,row, buttonM, buttonR);
                                        closer.closeModal();
                                    }
                                }
                            });
                    GWT.log("[handleModalButton]Get entity: "+ manchesterSyntaxFrameEditor.getValue());
                }else {
                    closer.closeModal();
                }
            }
        };
        modalPresenter.setPrimaryButton(DialogButton.OK);
        modalPresenter.setButtonHandler(DialogButton.OK, r);

    }

    private void deleteRepairAxiom(SafeHtml selectedAxiom){
        axiomsToModify.remove(selectedAxiom);
        axiomsToDelete.add(selectedAxiom);
    }

    private void redoRepair(SafeHtml selectedAxiom){
        axiomsToDelete.remove(selectedAxiom);
        axiomsToModify.remove(selectedAxiom);
    }


    public RepairDetails getRepairDetails() {
        return new RepairDetails(axiomsToDelete,axiomsToModify);
    }

    public void removeRepairDetails(){
        axiomsToModify.clear();
        axiomsToDelete.clear();
    }

    public RepairInterfaceViewImpl getView() {
        return view;
    }

    @Override
    public void clearAxiomTable() {
        removeRepairDetails();
        view.table.removeAllRows();
    }

    @Override
    public void setAxioms(DebuggingSessionStateResult debuggingSessionStateResult) {

    }

    @Override
    public void setEnabledButton(String buttonTyp) {

    }
}
