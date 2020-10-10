package edu.stanford.bmir.protege.web.client.debugger;

import com.google.gwt.core.client.GWT;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ProjectSingleton
public class DebuggerResultManager {

    private List<DebuggerPresenter> presenterList = new ArrayList<>();

    DebuggingSessionStateResult debuggingSessionStateResult;

    @Inject
    public DebuggerResultManager(){}

    public void addToList(DebuggerPresenter debuggerPresenter){
        presenterList.add(debuggerPresenter);
    }

    public void setDebuggingSessionStateResult(DebuggingSessionStateResult debuggingSessionStateResult) {
        GWT.log("[DebuggerResultManager] get result "+ debuggingSessionStateResult);
        this.debuggingSessionStateResult = debuggingSessionStateResult;
    }

    public void updateContent(){
        clearAxioms();
        for (DebuggerPresenter debuggerpresenter:
             presenterList) {
            debuggerpresenter.setAxoims(debuggingSessionStateResult);
        }
    }
    public void clearAxioms(){
        for (DebuggerPresenter debuggerpresenter:
                presenterList) {
            debuggerpresenter.clearAxiomtable();
        }
    }

}
