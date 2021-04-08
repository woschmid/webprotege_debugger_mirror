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
import edu.stanford.bmir.protege.web.shared.debugger.SessionState;
import edu.stanford.bmir.protege.web.shared.debugger.SetPreferencesAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static edu.stanford.bmir.protege.web.client.library.msgbox.MessageStyle.QUESTION;

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

    private ModalCloser closer;


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
        Preferences preferences = debuggerResultManager.getDebuggingSessionStateResult().getPreferences();
        setPreference(preferences);
        view.setReset(this::handleReset);
    }


    public void handleSubmitPreference() {
        try{
            Preferences newPreference = getPreferences();
            if (newPreference.equals(debuggerResultManager.getDebuggingSessionStateResult().getPreferences())){
                closer.closeModal();
            }else{
                if (!(debuggerResultManager.getDebuggingSessionStateResult().getSessionState() == SessionState.INIT ||
                        debuggerResultManager.getDebuggingSessionStateResult().getSessionState() == SessionState.STOPPED)){
                    messageBox.showYesNoConfirmBox("Information","Are you sure to change the preference? The Debugging session will be terminated",this::submitPreference);
                    messageBox.showAlert("");
                }else{
                    submitPreference();
                }
            }
        } catch (IllegalArgumentException e){
            messageBox.showErrorMessage("Wrong Arguments.", e);
            messageBox.showErrorMessage("",e);
        }
    }

    private void submitPreference(){
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
        closer.closeModal();
    }

    private Preferences getPreferences() {
        Preferences preferences = new Preferences(Long.parseLong(view.getDSKTtextBox()), Long.parseLong(view.getRTtextBox()), Integer.parseInt(view.getMVPFAtextBox()), Integer.parseInt(view.getMVCAtextBox()),view.getReseanerFild(),Integer.parseInt(view.getMNODtextBox()));
        GWT.log("Preferences: " + preferences);
        return preferences;
    }

    public ConfigureDebuggerViewImpl getView(){
        return view;
    }

    private void handleReset(){
        try{
            Preferences preferences = getPreferences();
            preferences.reset();
            setPreference(preferences);
        } catch (IllegalArgumentException e){
            messageBox.showErrorMessage("Wrong Arguments.", e);
        }
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

    public void setCloser(ModalCloser closer) {
        this.closer = closer;
    }

    private void setPreference(Preferences preferences){
        view.ReseanerFild.clear();
        view.DSKTtextBox.setText(preferences.getSessionKeepAliveInMillis().toString());
        view.MVCAtextBox.setText(String.valueOf(preferences.getMaxVisibleCorrectAxioms()));
        view.MVPFAtextBox.setText(String.valueOf(preferences.getMaxVisiblePossiblyFaultyAxioms()));
        view.RTtextBox.setText(preferences.getReasonerTimeoutInMillis().toString());
        view.MNODtextBox.setText(String.valueOf(preferences.getMaxNumberOfDiagnoses()));
        String[] reasoners = preferences.getReasoners();
        int index = 0;
        int i = 0;
        for (String resoner:
                reasoners) {
            view.ReseanerFild.addItem(resoner);
            if (preferences.getReasonerId().equals(resoner)){
                index = i;
            }
            i++;
        }
        view.ReseanerFild.setSelectedIndex(index);

    }
}
