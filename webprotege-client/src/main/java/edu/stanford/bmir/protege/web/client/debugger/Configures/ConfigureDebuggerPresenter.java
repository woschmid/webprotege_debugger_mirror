package edu.stanford.bmir.protege.web.client.debugger.Configures;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerPresenter;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerResultManager;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallbackWithProgressDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.library.modal.ModalButtonHandler;
import edu.stanford.bmir.protege.web.client.library.modal.ModalCloser;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.Preferences;
import edu.stanford.bmir.protege.web.shared.debugger.SetPreferencesAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigureDebuggerPresenter extends DebuggerPresenter {

    @Nonnull
    private final ModalManager modalManager;

    private DispatchServiceManager dsm;

    private LoggedInUserProvider loggedInUserProvider;


    private DispatchErrorMessageDisplay errorDisplay;


    private ProgressDisplay progressDisplay;

    private Set<SafeHtml> axiomsToDelete = new HashSet<>();

    private Map<SafeHtml, String> axiomsToModify = new HashMap<>();

    interface HandleModalButton extends ModalButtonHandler {
        void handleModalButton(@Nonnull ModalCloser closer);
    }

    @Nonnull
    private ProjectId projectId;

    private ConfigureDebuggerViewImpl view;


    @Inject
    public ConfigureDebuggerPresenter(@Nonnull ModalManager modalManager, @Nonnull DispatchServiceManager dsm,
                                      @Nonnull LoggedInUserProvider loggedInUserProvider,
                                      @Nonnull DispatchErrorMessageDisplay errorDisplay,
                                      @Nonnull ProgressDisplay progressDisplay,
                                      @Nonnull ProjectId projectId, ConfigureDebuggerViewImpl view,
                                      MessageBox messageBox, StatementPresenter statementPresenter, DebuggerResultManager debuggerResultManager
                                     ){
        super(statementPresenter, debuggerResultManager,view,loggedInUserProvider,errorDisplay,progressDisplay,messageBox);
        this.progressDisplay = progressDisplay;
        this.modalManager = modalManager;
        this.dsm = dsm;
        this.view = view;
        this.projectId = projectId;
    }

    public void run(){
        view.setLimitToInput();
        view.DSKTtextBox.setText(debuggerResultManager.getDebuggingSessionStateResult().getPreferences().getSessionKeepaliveInMillis().toString());
        view.MVCAtextBox.setText(String.valueOf(debuggerResultManager.getDebuggingSessionStateResult().getPreferences().getMaxVisibleCorrectAxioms()));
        view.MVPFAtextBox.setText(String.valueOf(debuggerResultManager.getDebuggingSessionStateResult().getPreferences().getMaxVisiblePossiblyFaultyAxioms()));
        view.RTtextBox.setText(debuggerResultManager.getDebuggingSessionStateResult().getPreferences().getReasonerTimeoutInMillis().toString());
    }

    public void handleSubmitPreference() {
        this.dsm.execute(new SetPreferencesAction(projectId,getPreferences()),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Setting the Preferences";
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

    private Preferences getPreferences() {
        Preferences preferences = new Preferences(Long.parseLong(view.getDSKTtextBox()), Long.parseLong(view.getRTtextBox()), Integer.parseInt(view.getMVPFAtextBox()), Integer.parseInt(view.getMVCAtextBox()));
        /*
        preferences.setMaxVisibleCorrectAxioms(Integer.parseInt(view.getMVCAtextBox()));
        preferences.setMaxVisiblePossiblyFaultyAxioms(Integer.parseInt(view.getMVPFAtextBox()));
        preferences.setSessionKeepaliveInMillis(Long.parseLong(view.getDSKTtextBox()));
        preferences.setReasonerTimeoutInMillis(Long.parseLong(view.getRTtextBox()));
        */
        GWT.log("Preferences: " + preferences);
        return preferences;
    }

    public ConfigureDebuggerViewImpl getView(){
        return view;
    }

    @Override
    public void clearAxiomTable() {

    }

    @Override
    public void setAxioms(DebuggingSessionStateResult debuggingSessionStateResult) {

    }

    @Override
    public void setEnabledButton(String buttonTyp) {

    }
}
