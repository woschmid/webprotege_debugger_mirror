package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;

public class StartDebuggingResult implements Result {

    private String msg;

    @GwtSerializationConstructor
    private StartDebuggingResult() {
    }

    public StartDebuggingResult(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
