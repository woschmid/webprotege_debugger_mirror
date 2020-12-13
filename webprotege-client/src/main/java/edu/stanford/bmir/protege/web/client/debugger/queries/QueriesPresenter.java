package edu.stanford.bmir.protege.web.client.debugger.queries;

import com.google.common.collect.ImmutableMap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.debugger.ConfigureDebuggerView;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerPresenter;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerResultManager;
import edu.stanford.bmir.protege.web.client.debugger.repairInterface.RepairInterfacePresenter;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementView;
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
import edu.stanford.bmir.protege.web.client.portlet.HasPortletActions;
import edu.stanford.bmir.protege.web.client.portlet.PortletAction;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.debugger.*;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueriesPresenter extends DebuggerPresenter {

    @Nonnull
    ManchesterSyntaxFrameEditorPresenter manchesterSyntaxFrameEditorPresenter;

    private StatementPresenter statementPresenter;

    private QueriesView view;

    private LoggedInUserProvider loggedInUserProvider;


    @Nonnull
    private ProjectId projectId;

    @Nonnull
    private final ModalManager modalManager;

    private DispatchServiceManager dsm;

    @Nonnull
    private final ConfigureDebuggerView configureDebuggerView;

    @Nonnull  RepairInterfacePresenter repairInterfacePresenter;


    @Inject
    public QueriesPresenter(@Nonnull ProjectId projectId,
                            DispatchServiceManager dispatchServiceManager,
                            MessageBox messageBox, StatementPresenter statementPresenter, RepairInterfacePresenter repairInterfacePresenter,
                            DispatchErrorMessageDisplay errorDisplay, ProgressDisplay progressDisplay, DebuggerResultManager debuggerResultManager, QueriesView view, LoggedInUserProvider loggedInUserProvider, @Nonnull ModalManager modalManager, @Nonnull ConfigureDebuggerView configureDebuggerView) {
        super(statementPresenter, debuggerResultManager,view,loggedInUserProvider,errorDisplay,progressDisplay,messageBox);
        this.projectId = projectId;
        this.loggedInUserProvider = loggedInUserProvider;
//        this.debuggerResultManager = debuggerResultManager;
        this.dsm = dispatchServiceManager;
        this.statementPresenter = statementPresenter;
        this.view = view;
        this.modalManager = modalManager;
        this.configureDebuggerView = configureDebuggerView;
        this.repairInterfacePresenter = repairInterfacePresenter;
        statementPresenter.addCheckBoxClickhandler(this::CheckCheckBox);
    }

    public StatementPresenter getStatementPresenter() {
        return statementPresenter;
    }


    WebProtegeEventBus eventBus;
    public void start(AcceptsOneWidget container, WebProtegeEventBus eventBus) {
        super.start(container,eventBus);
        this.eventBus = eventBus;
        setEnabledButton("stop");
        this.view.setStartDebuggingHandler(this::startDebugging);
        this.view.setStopDebuggingHandler(this::stopDebugging);
        this.view.setSubmitDebuggingHandler(this::submitDebugging);
        this.view.setRepairDebuggingHandler(this::RepairDebugging);
        this.view.setCheckOntologyHandler(this::checkOntology);
        this.view.setEditSettingHandler(this::ConfigureTimeout);
        this.view.setHelpHandler(this::showHelp);
        reload();
    }

    private void checkOntology() {
        GWT.log("[DebuggerPresenter]Check Button pressed!!!!!");
        this.dsm.execute(new CheckOntologyAction(projectId),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Check Ontology";
                    }

                    @Override
                    public String getProgressDisplayMessage() {
                        return "Please wait";
                    }

                    public void handleSuccess(DebuggingSessionStateResult debuggingSessionStateResult) {
                        GWT.log("[DebuggerPresenter]Start Debugging Button pressed!!!!!" + debuggingSessionStateResult);
                        handlerDebugging(debuggingSessionStateResult);
                    }
                });
    }

    private void startDebugging() {
        GWT.log("[DebuggerPresenter]Start Debugging Button pressed!!!!!");
        this.dsm.execute(new StartDebuggingAction(projectId),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Start Debugging";
                    }

                    @Override
                    public String getProgressDisplayMessage() {
                        return "Please wait";
                    }

                    public void handleSuccess(DebuggingSessionStateResult debuggingSessionStateResult) {
                        GWT.log("[DebuggerPresenter]Start Debugging Button pressed!!!!!" + debuggingSessionStateResult);
                        handlerDebugging(debuggingSessionStateResult);
                    }
                });
    }

    private void stopDebugging() {
        GWT.log("[QueriesPresenter]Stop Debugging Button pressed!!!!!");

        this.dsm.execute(new StopDebuggingAction(projectId),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Stop debugging";
                    }

                    @Override
                    public String getProgressDisplayMessage() {
                        return "Please wait";
                    }

                    public void handleSuccess(DebuggingSessionStateResult debuggingSessionStateResult) {
                        handlerDebugging(debuggingSessionStateResult);
                        debuggerResultManager.clearAxioms();
                    }
                });

    }

    private void reload(){
        GWT.log("[QueriesPresenter]reload Debugging!!!!!");
        this.dsm.execute(new ReloadDebuggerAction(projectId), debuggingSessionStateResult -> {
            handlerDebugging(debuggingSessionStateResult);
        });
    }
    private void submitDebugging() {
        GWT.log("[QueriesPresenter]Submit Debugging Button pressed!!!!!");

        this.dsm.execute(new SubmitDebuggingAction(projectId, getAnswers()),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingSessionStateResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Submit axioms";
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

    private void RepairDebugging() {
        GWT.log("[QueriesPresenter]Repair Debugging Button pressed!!!!!");
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle("Repair");
        repairInterfacePresenter.start(eventBus,debuggerResultManager.getDebuggingSessionStateResult());
        modalPresenter.setView(repairInterfacePresenter.getView());
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.OK);
        modalPresenter.setButtonHandler(DialogButton.OK,
                this::reload);
        modalManager.showModal(modalPresenter);

//        messageBox.showYesNoConfirmBox("Repair Ontology", "Do repairing?",this::runRepair );

    }

    private void reload(ModalCloser modalCloser) {
        modalCloser.closeModal();
        reload();
    }

    private void runRepair(){
        this.dsm.execute(new RepairAction(projectId),
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
                        handlerDebugging(debuggingSessionStateResult);
                    }
                });
    }

    private ImmutableMap<SafeHtml, Boolean> getAnswers() {
        Map<SafeHtml, Boolean> allSelectQueries = getStatementPresenter().getTableInfo();
        GWT.log("[QueriesPresenter] Selected Queries are "+ allSelectQueries.toString());
        return new ImmutableMap.Builder<SafeHtml, Boolean>().putAll(allSelectQueries).build();
    }

    public void CheckCheckBox(ClickEvent event, CheckBox checkBoxOther, List<CheckBox> list) {
        boolean checked = ((CheckBox)event.getSource()).getValue();
        if (checked){
            checkBoxOther.setValue(false);
//            setEnabledButton("submit");
        }
        boolean flag = false;
        for ( CheckBox checkbox:
             list) {
            flag = checkbox.getValue() || flag;
        }
        if(flag){
            setEnabledButton("submit");
        }else{
            setEnabledButton("unsubmit");
        }
    }

    public void setAxioms(DebuggingSessionStateResult debuggingSessionStateResult){
        Query msg = debuggingSessionStateResult.getQuery();
        if (msg != null) {
            Set<SafeHtml> items = msg.getAxioms();
            getStatementPresenter().addQueriesStatement(items);
        }
    }

    public void clearAxiomTable(){
        statementPresenter.clearAxoim();
    }

    public void setEnabledButton(String buttonTyp){
        switch (buttonTyp) {
            case "stop":
                view.disablebutton("stop");
                view.disablebutton("submit");
                view.enablebutton("start");
                view.disablebutton("repair");
                view.changeStartButton(false);
                break;
            case "start":
                view.enablebutton("stop");
                view.disablebutton("submit");
                view.disablebutton("start");
                view.disablebutton("repair");
                view.changeStartButton(true);
                break;
            case "submit":
                view.enablebutton("submit");
                view.disablebutton("repair");
                view.changeStartButton(true);
                break;
            case "unsubmit":
                view.disablebutton("submit");
                break;
            case "locked":
                view.disablebutton("start");
                view.disablebutton("stop");
                view.disablebutton("submit");
                view.disablebutton("repair");
                view.changeStartButton(false);
                break;
            case "repair":
                view.disablebutton("start");
                view.enablebutton("stop");
                view.disablebutton("submit");
                view.enablebutton("repair");
                view.changeStartButton(true);
                break;
        }
    }

    private void ConfigureTimeout() {
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle("Configure Timeout");
        modalPresenter.setView(configureDebuggerView);
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.OK);
        modalPresenter.setButtonHandler(DialogButton.OK,
                this::handleModalButton);
        modalManager.showModal(modalPresenter);

    }

    private void showHelp(){
        Window.open("https://git-ainf.aau.at/interactive-KB-debugging/debugger/-/wikis/input-ontology","_blank","");
    }

    private void handleModalButton(ModalCloser closer) {
        closer.closeModal();
    }

    public void setManchesterSyntaxFrameEditorPresenter(ManchesterSyntaxFrameEditorPresenter manchesterSyntaxFrameEditorPresenter) {
        this.manchesterSyntaxFrameEditorPresenter = manchesterSyntaxFrameEditorPresenter;
    }
}
