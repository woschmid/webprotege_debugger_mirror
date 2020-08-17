package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;

public class StopDebuggingResult implements Result {

    private String msg;

    @GwtSerializationConstructor
    private StopDebuggingResult() {
    }

    public StopDebuggingResult(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
