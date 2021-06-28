package edu.stanford.bmir.protege.web.client.shaclTool.shaclEditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallbackWithProgressDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.portlet.PortletUi;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.debugger.CheckOntologyAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.shacl.ShaclValidationResult;
import edu.stanford.bmir.protege.web.shared.shacl.ValidateShaclAction;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class ShaclEditorPresenter {

    @Nonnull
    private ProjectId projectId;

    private LoggedInUserProvider loggedInUserProvider;

    private DispatchServiceManager dsm;

    private ShaclViewImpl view;

    private DispatchErrorMessageDisplay errorDisplay;

    private ProgressDisplay progressDisplay;

    WebProtegeEventBus eventBus;

    @Inject
    public ShaclEditorPresenter(@Nonnull ProjectId projectId,
                                DispatchServiceManager dispatchServiceManager,
                                ShaclViewImpl view,
                                LoggedInUserProvider loggedInUserProvider,
                                DispatchErrorMessageDisplay errorDisplay,
                                ProgressDisplay progressDisplay){
        this.projectId = projectId;
        this.loggedInUserProvider = loggedInUserProvider;
        this.dsm = dispatchServiceManager;
        this.view = view;
        this.errorDisplay = errorDisplay;
        this.progressDisplay = progressDisplay;

    }

    public void start(AcceptsOneWidget container, WebProtegeEventBus eventBus) {
        container.setWidget(view.asWidget());
        this.eventBus = eventBus;
        view.setShaclValidateHandler(this::shaclValidate);
    }

    private void shaclValidate(){
        GWT.log("[ShaclEditorPresenter]shaclValidate!!!!!");
        this.dsm.execute(new ValidateShaclAction(projectId, view.getContent()),
                new DispatchServiceCallbackWithProgressDisplay<ShaclValidationResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Validate Ontology";
                    }

                    @Override
                    public String getProgressDisplayMessage() {
                        return "Please wait";
                    }

                    public void handleSuccess(ShaclValidationResult shaclValidationResult) {
                        GWT.log("[ShaclEditorPresenter]shaclValidate Button pressed!!!!!" + shaclValidationResult.getValidationResult());
                    }
                });
    }
}
