package edu.stanford.bmir.protege.web.client.debugger;

import com.google.common.collect.ImmutableMap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.debugger.queries.QueriesPresenter;
import edu.stanford.bmir.protege.web.client.debugger.repairs.RepairsPresenter;
import edu.stanford.bmir.protege.web.client.debugger.testcases.TestcasesPresenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
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
import java.util.*;
import java.util.function.Consumer;

/**
 * Author: Matthew Horridge<br> Stanford University<br> Bio-Medical Informatics Research Group<br> Date: 12/09/2013
 */
public class DebuggerPresenter{

    private final DispatchServiceManager dsm;

    private final DebuggerViewImpl view;

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
                             QueriesPresenter queriesPresenter, RepairsPresenter repairsPresenter, TestcasesPresenter testcasesPresenter) {
        this.projectId = projectId;
        this.view = view;
        this.dsm = dispatchServiceManager;
        this.queriesPresenter = queriesPresenter;
        this.repairsPresenter = repairsPresenter;
        this.testcasesPresenter = testcasesPresenter;
    }

    public void start(AcceptsOneWidget container, WebProtegeEventBus eventBus) {
        GWT.log("[DebuggerPresenter] Started Debugger");
        queriesPresenter.start(view.getQueriesContainer(), this::startDebugging, this::stopDebugging,this::submitDebugging);
        repairsPresenter.start(view.getRepairsContainer());
        testcasesPresenter.start(view.getTestcasesContainer());
        container.setWidget(view.asWidget());
    }

    private void startDebugging() {
        GWT.log("[DebuggerPresenter]Start Debugging Button pressed!!!!!");
        this.dsm.execute(new StartDebuggingAction(projectId), new Consumer<DebuggingResult>() {
            @Override
            public void accept(DebuggingResult debuggingResult) {
                DebuggerPresenter.this.setQueriesStatement(debuggingResult.getQuery());
                DebuggerPresenter.this.setReqairsStatement(debuggingResult.getDiagnoses());
                DebuggerPresenter.this.setTestCasesStatement(debuggingResult.getPositiveTestCases(),debuggingResult.getNegativeTestCases());

            }
        });
        queriesPresenter.setEnabledButton("start");
    }

    private void stopDebugging() {
        GWT.log("[QueriesPresenter]Stop Debugging Button pressed!!!!!");
        this.dsm.execute(new StopDebuggingAction(projectId), new Consumer<DebuggingResult>() {
            @Override
            public void accept(DebuggingResult stopDebuggingResult) {
//                QueriesPresenter.this.onSuccess(stopDebuggingResult.getMsg());
            }
        });
        clearAxiomtabel();
        queriesPresenter.setEnabledButton("stop");
    }

    private void submitDebugging() {
        GWT.log("[QueriesPresenter]Submit Debugging Button pressed!!!!!");
        this.dsm.execute(new SubmitDebuggingAction(projectId, getAnswers()), new Consumer<DebuggingResult>() {
            @Override
            public void accept(DebuggingResult submitDebuggingResult) {
                DebuggerPresenter.this.setQueriesStatement(submitDebuggingResult.getQuery());
                DebuggerPresenter.this.setReqairsStatement(submitDebuggingResult.getDiagnoses());
                DebuggerPresenter.this.setTestCasesStatement(submitDebuggingResult.getPositiveTestCases(),submitDebuggingResult.getNegativeTestCases());
            }
        });
        clearAxiomtabel();
        queriesPresenter.setEnabledButton("start");
    }

    private ImmutableMap<String, Boolean> getAnswers() {
        Map allSelectQueries = queriesPresenter.getStatementPresenter().getTableInfo();
        GWT.log("[QueriesPresenter] Selected Queries are "+ allSelectQueries.toString());
        ImmutableMap<String, Boolean> answers = new ImmutableMap.Builder<String, Boolean>().putAll(allSelectQueries).build();
        return answers;
    }

    private void setQueriesStatement(Query msg){
        if (msg != null) {
            //Set<String> items = new HashSet<String>(Arrays.asList(msg.split(", ")));
            Set<String> items = msg.getAxioms();
            queriesPresenter.getStatementPresenter().addQueriesStatement(items);
        }
    }

    private void setReqairsStatement(List<Diagnosis> msg){
//        GWT.log("[DebuggerPresenter]setReqairsStatement's msg is "+ msg);
        //Set<String> items = new HashSet<String>(Arrays.asList(msg.split(", ")));
        //Set<String> items =
        if (msg != null) {
            Set<String> items = new HashSet<>();
            for (Diagnosis diagnosis : msg) {
                items.addAll(diagnosis.getAxioms());
            }
            repairsPresenter.getStatementPresenter().addRepairsStatement(items);
        }
    }
/*
    private void setTestCasesStatement(String msgP, String msgN){
        Set<String> itemsP = new HashSet<String>(Arrays.asList(msgP.split(", ")));
        Set<String> itemsN = new HashSet<String>(Arrays.asList(msgN.split(", ")));
        testcasesPresenter.getStatementPresenter1().addTestCasesStatement(itemsP);
        testcasesPresenter.getStatementPresenter2().addTestCasesStatement(itemsN);
    }
*/
    private void setTestCasesStatement(List<TestCase> msgP, List<TestCase> msgN){
        //Set<String> itemsP = new HashSet<String>(Arrays.asList(msgP.split(", ")));
        //Set<String> itemsN = new HashSet<String>(Arrays.asList(msgN.split(", ")));
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

}
