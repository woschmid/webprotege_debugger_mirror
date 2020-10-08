package edu.stanford.bmir.protege.web.client.debugger;

import com.google.gwt.core.client.GWT;
import edu.stanford.bmir.protege.web.client.debugger.queries.QueriesPresenter;
import edu.stanford.bmir.protege.web.client.debugger.repairs.RepairsPresenter;
import edu.stanford.bmir.protege.web.client.debugger.testcases.TestcasesPresenter;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;

import javax.inject.Inject;

@ProjectSingleton
public class DebuggerResultManager {
    private QueriesPresenter queriesPresenter;

    private RepairsPresenter repairsPresenter;

    private TestcasesPresenter testcasesPresenter;

    DebuggingSessionStateResult debuggingSessionStateResult;

    @Inject
    public DebuggerResultManager(){}

    public void setQueriesPresenter(QueriesPresenter queriesPresenter) {
        this.queriesPresenter = queriesPresenter;
    }

    public void setRepairsPresenter(RepairsPresenter repairsPresenter) {
        this.repairsPresenter = repairsPresenter;
    }

    public void setTestcasesPresenter(TestcasesPresenter testcasesPresenter) {
        this.testcasesPresenter = testcasesPresenter;
    }

    public DebuggingSessionStateResult getDebuggingSessionStateResult() {
        return debuggingSessionStateResult;
    }

    public void setDebuggingSessionStateResult(DebuggingSessionStateResult debuggingSessionStateResult) {
        GWT.log("[DebuggerResultManager] get result "+ debuggingSessionStateResult);
        this.debuggingSessionStateResult = debuggingSessionStateResult;
    }

    public void updateContent(){
        queriesPresenter.setQueriesStatement(debuggingSessionStateResult.getQuery());
        repairsPresenter.getStatementPresenter().addRepairsStatement(debuggingSessionStateResult.getDiagnoses());
    }
}
