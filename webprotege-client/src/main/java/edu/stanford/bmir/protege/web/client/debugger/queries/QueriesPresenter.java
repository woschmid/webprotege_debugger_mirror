package edu.stanford.bmir.protege.web.client.debugger.queries;

import com.google.common.collect.ImmutableMap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.CheckBox;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerPresenter;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerResultManager;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallbackWithProgressDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
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

    private StatementPresenter statementPresenter;

    private QueriesView view;

    private DispatchServiceManager dsm;

    private DispatchErrorMessageDisplay errorDisplay;

    private ProgressDisplay progressDisplay;

    private LoggedInUserProvider loggedInUserProvider;

    private DebuggerResultManager debuggerResultManager;

    private MessageBox messageBox;

    @Nonnull
    private ProjectId projectId;


    @Inject
    public QueriesPresenter(@Nonnull ProjectId projectId,
                            DispatchServiceManager dispatchServiceManager,
                            MessageBox messageBox, StatementPresenter statementPresenter,
                            DispatchErrorMessageDisplay errorDisplay, ProgressDisplay progressDisplay, DebuggerResultManager debuggerResultManager, QueriesView view, LoggedInUserProvider loggedInUserProvider) {
        super(statementPresenter, debuggerResultManager,view,loggedInUserProvider);
        this.projectId = projectId;
        this.loggedInUserProvider = loggedInUserProvider;
        this.errorDisplay= errorDisplay;
        this.progressDisplay = progressDisplay;
        this.messageBox = messageBox;
        this.debuggerResultManager = debuggerResultManager;
        this.dsm = dispatchServiceManager;
        this.statementPresenter = statementPresenter;
        this.view = view;
        statementPresenter.addCheckBoxClickhandler(this::CheckCheckBox);
    }

    public StatementPresenter getStatementPresenter() {
        return statementPresenter;
    }



    public void start(AcceptsOneWidget container, WebProtegeEventBus eventBus) {
        super.start(container,eventBus);
        setEnabledButton("stop");
        this.view.setStartDebuggingHandler(this::startDebugging);
        this.view.setStopDebuggingHandler(this::stopDebugging);
        this.view.setSubmitDebuggingHandler(this::submitDebugging);
        this.view.setRepairDebuggingHandler(this::RepairDebugging);
        reload();
    }

    private void startDebugging() {
        GWT.log("[DebuggerPresenter]Start Debugging Button pressed!!!!!");
        GWT.log("[DebuggerPresenter]dsm!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+dsm);
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
            if (!debuggingSessionStateResult.getUserId().equals(loggedInUserProvider.getCurrentUserId())){
                messageBox.showAlert("Can not start!","This session is started by other user: " + debuggingSessionStateResult.getUserId().getUserName());
                setEnabledButton("locked");
            }else {
                handlerDebugging(debuggingSessionStateResult);
            }

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

        messageBox.showYesNoConfirmBox("Repair Ontology", "Do repairing?",this::runRepair );

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


    private void handlerDebugging(DebuggingSessionStateResult debuggingSessionStateResult) {
        boolean isStop = false;
        if (debuggingSessionStateResult.getSessionState() == SessionState.STOPPED) {
            isStop = true;
        }
//        clearAxiomtabel();
        if (!debuggingSessionStateResult.isOk()) {
            messageBox.showAlert("Can not start!", debuggingSessionStateResult.getMessage());
            setEnabledButton("locked");
        } else {
            if (debuggingSessionStateResult.getMessage() != null) {
                messageBox.showAlert("Information", debuggingSessionStateResult.getMessage());
            }
            GWT.log("[QueriesPresenter]debuggingSessionStateResult is "+ debuggingSessionStateResult);
            debuggerResultManager.setDebuggingSessionStateResult(debuggingSessionStateResult);
            showResults(debuggingSessionStateResult, isStop);
            checkFinal(debuggingSessionStateResult);
        }

    }

    private void showResults(DebuggingSessionStateResult debuggingSessionStateResult, boolean isStop) {
        if(!isStop){
            debuggerResultManager.updateContent();
        }
        changeSessionState(debuggingSessionStateResult.getSessionState());
    }

    private void checkFinal(DebuggingSessionStateResult result){
        if (result.getSessionState() == SessionState.STARTED &&
                (result.getQuery() == null && result.getDiagnoses() != null &&
                        result.getDiagnoses().size() == 1)){
            setEnabledButton("repair");
        }else{
            changeSessionState(result.getSessionState());
        }

    }

    private void changeSessionState(SessionState state){
        if(state == SessionState.STARTED || state == SessionState.COMPUTING){
            setEnabledButton("start");
        }else if (state == SessionState.STOPPED){
            setEnabledButton("stop");
        }else if (state == SessionState.INIT){
            setEnabledButton("start");
        }
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

    public void setAxoims(DebuggingSessionStateResult debuggingSessionStateResult){
        Query msg = debuggingSessionStateResult.getQuery();
        if (msg != null) {
            Set<SafeHtml> items = msg.getAxioms();
            getStatementPresenter().addQueriesStatement(items);
        }
    }

    public void clearAxiomtable(){
        statementPresenter.clearAxoim();
    }

    public void setEnabledButton(String buttonTyp){
        switch (buttonTyp) {
            case "stop":
                view.disablebutton("stop");
                view.disablebutton("submit");
                view.enablebutton("start");
                view.disablebutton("repair");
                break;
            case "start":
                view.enablebutton("stop");
                view.disablebutton("submit");
                view.disablebutton("start");
                view.disablebutton("repair");
                break;
            case "submit":
                view.enablebutton("submit");
                view.disablebutton("repair");
                break;
            case "unsubmit":
                view.disablebutton("submit");
                break;
            case "locked":
                view.disablebutton("start");
                view.disablebutton("stop");
                view.disablebutton("submit");
                view.disablebutton("repair");
                break;
            case "repair":
                view.disablebutton("start");
                view.enablebutton("stop");
                view.disablebutton("submit");
                view.enablebutton("repair");
                break;
        }
    }

}
