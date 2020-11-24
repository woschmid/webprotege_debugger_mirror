package edu.stanford.bmir.protege.web.client.debugger.repairInterface;



import com.google.gwt.core.client.GWT;
import edu.stanford.bmir.protege.web.client.frame.ManchesterSyntaxFrameEditorPresenter;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalCloser;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class RepairInterfacePresenter{
    @Nonnull
    private final ModalManager modalManager;

    private RepairInterfaceViewImpl view;
    @Nonnull
    ManchesterSyntaxFrameEditorPresenter manchesterSyntaxFrameEditorPresenter;

    @Inject
    public RepairInterfacePresenter(@Nonnull ModalManager modalManager, @Nonnull ManchesterSyntaxFrameEditorPresenter manchesterSyntaxFrameEditorPresenter) {
        this.modalManager = modalManager;
        view = new RepairInterfaceViewImpl();
        this.manchesterSyntaxFrameEditorPresenter = manchesterSyntaxFrameEditorPresenter;
    }

    public void start(WebProtegeEventBus eventBus, DebuggingSessionStateResult debuggingSessionStateResult) {
        manchesterSyntaxFrameEditorPresenter.start(eventBus);
        view.setManchesterEditorHandler(this::manchesterEditor);
        setAxioms(debuggingSessionStateResult);

    }
    public void setAxioms(DebuggingSessionStateResult debuggingSessionStateResult){
        view.setAxioms(debuggingSessionStateResult.getDiagnoses().get(0).getAxioms());
    }

    private void manchesterEditor() {
        GWT.log("[RepairInterfacePresenter]+++++++++++++++++++ " + manchesterSyntaxFrameEditorPresenter.getView().getValue());
        manchesterSyntaxFrameEditorPresenter.getView().setValue("Class: ");
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle("Manchester Editor");
        modalPresenter.setView(manchesterSyntaxFrameEditorPresenter.getView());
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.OK);
        modalPresenter.setButtonHandler(DialogButton.OK,
                this::handleModalButton);
        modalManager.showModal(modalPresenter);

    }

    private void handleModalButton(ModalCloser closer) {
        GWT.log("[handleModalButton]Get entity: "+ manchesterSyntaxFrameEditorPresenter.getView().getValue());
        closer.closeModal();
    }

    public RepairInterfaceViewImpl getView() {
        return view;
    }
}
