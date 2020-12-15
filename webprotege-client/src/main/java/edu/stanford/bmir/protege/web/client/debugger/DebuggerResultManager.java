package edu.stanford.bmir.protege.web.client.debugger;

import com.google.gwt.core.client.GWT;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.SessionState;
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
            debuggerpresenter.setAxioms(debuggingSessionStateResult);
        }
    }
    public void clearAxioms(){
        for (DebuggerPresenter debuggerpresenter:
                presenterList) {
            debuggerpresenter.clearAxiomTable();
        }
    }
    public void changeButtonStatus(){
        SessionState state = debuggingSessionStateResult.getSessionState();
        GWT.log("[DebuggerResultManager] get result "+ debuggingSessionStateResult.getSessionState());
        for (DebuggerPresenter debuggerpresenter:
                presenterList) {
            if(state == SessionState.STARTED || state == SessionState.COMPUTING){
                debuggerpresenter.setEnabledButton("start");
            }else if (state == SessionState.STOPPED){
                debuggerpresenter.setEnabledButton("stop");
            }else if (state == SessionState.INIT){
                debuggerpresenter.setEnabledButton("init");
            }
        }

    }

    public DebuggingSessionStateResult getDebuggingSessionStateResult() {
        return debuggingSessionStateResult;
    }
}
