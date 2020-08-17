package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;

public class SubmitDebuggingResult implements Result {
    private String msg;

    @GwtSerializationConstructor
    private SubmitDebuggingResult() {
    }

    public SubmitDebuggingResult(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
