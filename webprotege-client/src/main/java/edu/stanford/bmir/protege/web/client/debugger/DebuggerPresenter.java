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
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingResult;
import edu.stanford.bmir.protege.web.shared.debugger.StartDebuggingAction;
import edu.stanford.bmir.protege.web.shared.debugger.StopDebuggingAction;
import edu.stanford.bmir.protege.web.shared.debugger.SubmitDebuggingAction;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
                DebuggerPresenter.this.setQueriesStatement(debuggingResult.getQuery().StringMsg());
                DebuggerPresenter.this.setReqairsStatement(debuggingResult.getDiagnoses().toString());
                DebuggerPresenter.this.setTestCasesStatement(debuggingResult.getPositiveTestCases().toString(),debuggingResult.getNegativeTestCases().toString());

            }
        });
        queriesPresenter.setEnabledButton(false);
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
        queriesPresenter.setEnabledButton(true);
    }

    private void submitDebugging() {
        GWT.log("[QueriesPresenter]Submit Debugging Button pressed!!!!!");
        this.dsm.execute(new SubmitDebuggingAction(projectId, getAnswers()), new Consumer<DebuggingResult>() {
            @Override
            public void accept(DebuggingResult submitDebuggingResult) {
//                QueriesPresenter.this.onSuccess(submitDebuggingResult.getMsg());
            }
        });
        clearAxiomtabel();
        queriesPresenter.setEnabledButton(true);
    }

    private ImmutableMap<String, Boolean> getAnswers() {
        // todo
        ImmutableMap<String, Boolean> answers = new ImmutableMap.Builder<String, Boolean>().put("hallo",true).build();
        return answers;
    }

    private void setQueriesStatement(String msg){
        Set<String> items = new HashSet<String>(Arrays.asList(msg.split(", ")));
        queriesPresenter.getStatementPresenter().addQueriesStatement(items);
    }

    private void setReqairsStatement(String msg){
//        GWT.log("[DebuggerPresenter]setReqairsStatement's msg is "+ msg);
        Set<String> items = new HashSet<String>(Arrays.asList(msg.split(", ")));
        repairsPresenter.getStatementPresenter().addRepairsStatement(items);
    }

    private void setTestCasesStatement(String msgP, String msgN){
        Set<String> itemsP = new HashSet<String>(Arrays.asList(msgP.split(", ")));
        Set<String> itemsN = new HashSet<String>(Arrays.asList(msgN.split(", ")));
        testcasesPresenter.getStatementPresenter1().addTestCasesStatement(itemsP);
        testcasesPresenter.getStatementPresenter2().addTestCasesStatement(itemsN);
    }
    private void clearAxiomtabel(){
        queriesPresenter.clearAxiomtable();
        repairsPresenter.clearAxiomtable();
        testcasesPresenter.clearAxiomtable();
    }

}
