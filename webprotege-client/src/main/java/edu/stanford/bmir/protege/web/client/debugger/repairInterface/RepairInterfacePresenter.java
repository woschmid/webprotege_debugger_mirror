package edu.stanford.bmir.protege.web.client.debugger.repairInterface;



import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.client.debugger.ConfigureDebuggerView;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallbackWithProgressDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.frame.ManchesterSyntaxFrameEditorPresenter;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalCloser;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.DeleteRepairAxiomAction;
import edu.stanford.bmir.protege.web.shared.debugger.RepairAction;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class RepairInterfacePresenter{
    @Nonnull
    private final ModalManager modalManager;

    private DispatchServiceManager dsm;

    private LoggedInUserProvider loggedInUserProvider;


    private DispatchErrorMessageDisplay errorDisplay;


    private ProgressDisplay progressDisplay;

    @Nonnull
    private ProjectId projectId;


    private RepairInterfaceViewImpl view;
    @Nonnull
    ManchesterSyntaxFrameEditorPresenter manchesterSyntaxFrameEditorPresenter;

    @Inject
    public RepairInterfacePresenter(@Nonnull ModalManager modalManager, @Nonnull DispatchServiceManager dsm,
                                    @Nonnull LoggedInUserProvider loggedInUserProvider,
                                    @Nonnull DispatchErrorMessageDisplay errorDisplay,
                                    @Nonnull ProgressDisplay progressDisplay,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull ManchesterSyntaxFrameEditorPresenter manchesterSyntaxFrameEditorPresenter) {

        this.errorDisplay =  errorDisplay;
        this.progressDisplay = progressDisplay;
        this.dsm = dsm;
        this.loggedInUserProvider = loggedInUserProvider;
        this.projectId = projectId;
        this.modalManager = modalManager;
        view = new RepairInterfaceViewImpl();
        this.manchesterSyntaxFrameEditorPresenter = manchesterSyntaxFrameEditorPresenter;
    }

    public void start(WebProtegeEventBus eventBus, DebuggingSessionStateResult debuggingSessionStateResult) {
        manchesterSyntaxFrameEditorPresenter.start(eventBus);
        view.setManchesterEditorHandler(this::manchesterEditor);
        view.setDeleteRepairHandler(this::deleteRepairAxiom);
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

    private void deleteRepairAxiom(SafeHtml selectedAxiom){
        this.dsm.execute(new DeleteRepairAxiomAction(projectId,selectedAxiom),
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
                        view.setAxioms(debuggingSessionStateResult.getDiagnoses().get(0).getAxioms());
                    }
                });
    }

    private void handleModalButton(ModalCloser closer) {
        GWT.log("[handleModalButton]Get entity: "+ manchesterSyntaxFrameEditorPresenter.getView().getValue());
        closer.closeModal();
    }

    public RepairInterfaceViewImpl getView() {
        return view;
    }
}
