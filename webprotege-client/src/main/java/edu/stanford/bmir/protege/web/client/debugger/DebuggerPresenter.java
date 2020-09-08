package edu.stanford.bmir.protege.web.client.debugger;

import com.google.common.collect.ImmutableMap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.debugger.queries.QueriesPresenter;
import edu.stanford.bmir.protege.web.client.debugger.repairs.RepairsPresenter;
import edu.stanford.bmir.protege.web.client.debugger.testcases.TestcasesPresenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallbackWithProgressDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.entity.CreateEntityPresenter;
import edu.stanford.bmir.protege.web.client.entity.EntityNodeUpdater;
import edu.stanford.bmir.protege.web.client.hierarchy.HierarchyFieldPresenter;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.permissions.LoggedInUserProjectPermissionChecker;
import edu.stanford.bmir.protege.web.client.selection.SelectionModel;
import edu.stanford.bmir.protege.web.shared.debugger.*;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Author: Matthew Horridge<br> Stanford University<br> Bio-Medical Informatics Research Group<br> Date: 12/09/2013
 */
public class DebuggerPresenter{

    private final DispatchServiceManager dsm;

    private final DebuggerViewImpl view;

    private final DispatchErrorMessageDisplay errorDisplay;

    private final ProgressDisplay progressDisplay;

    @Nonnull
    private final ProjectId projectId;

    private QueriesPresenter queriesPresenter;

    private RepairsPresenter repairsPresenter;

    private TestcasesPresenter testcasesPresenter;

    @Inject
    public DebuggerPresenter(DebuggerViewImpl view,
                             @Nonnull ProjectId projectId,
                             final SelectionModel selectionModel,
                             DispatchServiceManager dispatchServiceManager,
                             LoggedInUserProjectPermissionChecker permissionChecker,
                             HierarchyFieldPresenter hierarchyFieldPresenter,
                             Messages messages,
                             @Nonnull CreateEntityPresenter createEntityPresenter, EntityNodeUpdater entityNodeUpdater, MessageBox messageBox,
                             DispatchErrorMessageDisplay errorDisplay, ProgressDisplay progressDisplay, QueriesPresenter queriesPresenter, RepairsPresenter repairsPresenter, TestcasesPresenter testcasesPresenter) {
        this.projectId = projectId;
        this.view = view;
        this.dsm = dispatchServiceManager;
        this.errorDisplay = errorDisplay;
        this.progressDisplay = progressDisplay;
        this.queriesPresenter = queriesPresenter;
        this.repairsPresenter = repairsPresenter;
        this.testcasesPresenter = testcasesPresenter;
        GWT.log("[DebuggerPresenter] Started DebuggerPresenter");
    }

    public void start(AcceptsOneWidget container, WebProtegeEventBus eventBus) {
        GWT.log("[DebuggerPresenter] Started Debugger");
        queriesPresenter.start(view.getQueriesContainer(), this::startDebugging, this::stopDebugging,this::submitDebugging);
        repairsPresenter.start(view.getRepairsContainer());
        testcasesPresenter.start(view.getTestcasesContainer());
        container.setWidget(view.asWidget());
//        reload();
    }

    private void startDebugging() {
        GWT.log("[DebuggerPresenter]Start Debugging Button pressed!!!!!");
        this.dsm.execute(new StartDebuggingAction(projectId),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingResult>(errorDisplay,
                                                                                progressDisplay) {
            @Override
            public String getProgressDisplayTitle() {
                return "Start Debugging";
            }

            @Override
            public String getProgressDisplayMessage() {
                return "Please wait";
            }

            public void handleSuccess(DebuggingResult debuggingResult) {
                clearAxiomtabel();
                DebuggerPresenter.this.setQueriesStatement(debuggingResult.getQuery());
                DebuggerPresenter.this.setReqairsStatement(debuggingResult.getDiagnoses());
                DebuggerPresenter.this.setTestCasesStatement(debuggingResult.getPositiveTestCases(),debuggingResult.getNegativeTestCases());
                changeSessionState(debuggingResult.getSessionState());
            }
        });
    }

    private void stopDebugging() {
        GWT.log("[QueriesPresenter]Stop Debugging Button pressed!!!!!");

        this.dsm.execute(new StopDebuggingAction(projectId),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Stop debugging";
                    }

                    @Override
                    public String getProgressDisplayMessage() {
                        return "Please wait";
                    }

                    public void handleSuccess(DebuggingResult stopDebuggingResult) {
                        clearAxiomtabel();
                        changeSessionState(stopDebuggingResult.getSessionState());
                    }
                });

    }

    private void reload(){
        GWT.log("[QueriesPresenter]reload Debugging!!!!!");
        this.dsm.execute(new ReloadDebuggerAction(projectId), new Consumer<DebuggerStateResult>() {
            @Override
            public void accept(DebuggerStateResult debuggerStateResult) {
//                changeSessionState(debuggerStateResult.getSessionState());
            }
        });
    }
    private void submitDebugging() {
        GWT.log("[QueriesPresenter]Submit Debugging Button pressed!!!!!");

        this.dsm.execute(new SubmitDebuggingAction(projectId, getAnswers()),
                new DispatchServiceCallbackWithProgressDisplay<DebuggingResult>(errorDisplay,
                        progressDisplay) {
                    @Override
                    public String getProgressDisplayTitle() {
                        return "Submit axioms";
                    }

                    @Override
                    public String getProgressDisplayMessage() {
                        return "Please wait";
                    }

                    public void handleSuccess(DebuggingResult submitDebuggingResult) {
                        clearAxiomtabel();
                        DebuggerPresenter.this.setQueriesStatement(submitDebuggingResult.getQuery());
                        DebuggerPresenter.this.setReqairsStatement(submitDebuggingResult.getDiagnoses());
                        DebuggerPresenter.this.setTestCasesStatement(submitDebuggingResult.getPositiveTestCases(),submitDebuggingResult.getNegativeTestCases());
                        changeSessionState(submitDebuggingResult.getSessionState());
                    }
                });
    }

    private ImmutableMap<String, Boolean> getAnswers() {
        Map allSelectQueries = queriesPresenter.getStatementPresenter().getTableInfo();
        GWT.log("[QueriesPresenter] Selected Queries are "+ allSelectQueries.toString());
        ImmutableMap<String, Boolean> answers = new ImmutableMap.Builder<String, Boolean>().putAll(allSelectQueries).build();
        return answers;
    }

    private void setQueriesStatement(Query msg){
        if (msg != null) {
            Set<String> items = msg.getAxioms();
            queriesPresenter.getStatementPresenter().addQueriesStatement(items);
        }
    }

    private void setReqairsStatement(List<Diagnosis> msg){
        if (msg != null) {
            repairsPresenter.getStatementPresenter().addRepairsStatement(msg);
        }

    }

    private void setTestCasesStatement(List<TestCase> msgP, List<TestCase> msgN){
        Set<String> itemsP = new HashSet<>();
        if (msgP != null) {
            for (TestCase p : msgP) itemsP.add(p.getAxiom());
        }

        Set<String> itemsN = new HashSet<>();
        if (msgN != null) {
            for (TestCase n : msgN) itemsN.add(n.getAxiom());
        }

        testcasesPresenter.getStatementPresenter1().addTestCasesStatement(itemsP);
        testcasesPresenter.getStatementPresenter2().addTestCasesStatement(itemsN);
    }

    private void clearAxiomtabel(){
        queriesPresenter.clearAxiomtable();
        repairsPresenter.clearAxiomtable();
        testcasesPresenter.clearAxiomtable();
    }

    private void changeSessionState(SessionState state){
        if(state == SessionState.STARTED){
            queriesPresenter.setEnabledButton("start");
        }else if (state == SessionState.STOPPED){
            queriesPresenter.setEnabledButton("stop");
        }else if (state == SessionState.INIT){
            queriesPresenter.setEnabledButton("start");
        }
    }

}
