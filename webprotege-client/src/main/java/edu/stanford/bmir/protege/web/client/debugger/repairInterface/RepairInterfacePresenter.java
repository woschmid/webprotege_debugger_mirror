package edu.stanford.bmir.protege.web.client.debugger.repairInterface;



import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Button;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.frame.ManchesterSyntaxFrameEditorImpl;
import edu.stanford.bmir.protege.web.client.frame.ManchesterSyntaxFrameEditorPresenter;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalButtonHandler;
import edu.stanford.bmir.protege.web.client.library.modal.ModalCloser;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.DirtyChangedEvent;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.RepairDetails;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RepairInterfacePresenter{
    @Nonnull
    private final ModalManager modalManager;

    private DispatchServiceManager dsm;

    private LoggedInUserProvider loggedInUserProvider;


    private DispatchErrorMessageDisplay errorDisplay;


    private ProgressDisplay progressDisplay;

    private Set<SafeHtml> axiomsToDelete = new HashSet<SafeHtml>();

    private Map<SafeHtml, String> axiomsToModify = new HashMap<SafeHtml, String>();

    interface HandleModalButton extends ModalButtonHandler{
        void handleModalButton(@Nonnull ModalCloser closer);
    }

    @Nonnull
    private ProjectId projectId;


    private RepairInterfaceViewImpl view;

    @Nonnull
    ManchesterSyntaxFrameEditorPresenter manchesterSyntaxFrameEditorPresenter;
    @Nonnull
    ManchesterSyntaxFrameEditorImpl manchesterSyntaxFrameEditor;

    @Inject
    public RepairInterfacePresenter(@Nonnull ModalManager modalManager, @Nonnull DispatchServiceManager dsm,
                                    @Nonnull LoggedInUserProvider loggedInUserProvider,
                                    @Nonnull DispatchErrorMessageDisplay errorDisplay,
                                    @Nonnull ProgressDisplay progressDisplay,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull ManchesterSyntaxFrameEditorPresenter manchesterSyntaxFrameEditorPresenter,
                                    @Nonnull ManchesterSyntaxFrameEditorImpl manchesterSyntaxFrameEditor) {

        this.errorDisplay =  errorDisplay;
        this.progressDisplay = progressDisplay;
        this.dsm = dsm;
        this.loggedInUserProvider = loggedInUserProvider;
        this.projectId = projectId;
        this.modalManager = modalManager;
        view = new RepairInterfaceViewImpl();
        this.manchesterSyntaxFrameEditor = manchesterSyntaxFrameEditor;
    }

    public void start(WebProtegeEventBus eventBus, DebuggingSessionStateResult debuggingSessionStateResult) {
//        manchesterSyntaxFrameEditorPresenter.start(eventBus);
        view.setManchesterEditorHandler(this::manchesterEditor);
        view.setDeleteRepairHandler(this::deleteRepairAxiom);
        view.setRedoRepairHandler(this::redoRepair);
        setAxioms(debuggingSessionStateResult);
    }
    public void setAxioms(DebuggingSessionStateResult debuggingSessionStateResult){
        view.setAxioms(debuggingSessionStateResult.getDiagnoses().get(0).getAxioms());
    }

    private void manchesterEditor(SafeHtml selectedAxiom, String axiom, int row, Button buttonR) {
        manchesterSyntaxFrameEditor.clearValue();
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalManager.showModal(modalPresenter);
        modalPresenter.setTitle("Manchester Editor");
        modalPresenter.setView(manchesterSyntaxFrameEditor.asWidget());
        manchesterSyntaxFrameEditor.setValue(axiom);
        GWT.log("[handleModalButton]Get entity: "+ manchesterSyntaxFrameEditor.getValue());
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        HandleModalButton r = (ModalCloser closer) ->
        {
            GWT.log("[handleModalButton]Get entity: "+ manchesterSyntaxFrameEditor.getValue());
            axiomsToModify.put(selectedAxiom, manchesterSyntaxFrameEditor.getValue().get());
            view.changAxoim(manchesterSyntaxFrameEditor.getValue().get(),row,  buttonR);
            closer.closeModal();

        };
        modalPresenter.setPrimaryButton(DialogButton.OK);
        modalPresenter.setButtonHandler(DialogButton.OK, r);

    }

    private void deleteRepairAxiom(SafeHtml selectedAxiom){
        axiomsToDelete.add(selectedAxiom);
    }

    private void redoRepair(SafeHtml selectedAxiom){
        axiomsToDelete.remove(selectedAxiom);
        axiomsToModify.remove(selectedAxiom);
    }

    private void handleModalButton(ModalCloser closer) {
        GWT.log("[handleModalButton]Get entity: "+ manchesterSyntaxFrameEditor.getValue());
        closer.closeModal();
    }


    public RepairDetails getRepairDetails() {
        RepairDetails repairDetails = new RepairDetails(axiomsToDelete,axiomsToModify);
        return repairDetails;
    }

    public RepairInterfaceViewImpl getView() {
        return view;
    }
}
