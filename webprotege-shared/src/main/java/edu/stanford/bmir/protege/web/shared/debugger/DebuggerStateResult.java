package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;

import javax.annotation.Nonnull;

public class DebuggerStateResult implements Result {
    @Nonnull
    private SessionState sessionState;

    public DebuggerStateResult( @Nonnull SessionState sessionState) {
        this.sessionState = sessionState;
    }

    @Nonnull
    public SessionState getSessionState() {
        return sessionState;
    }

}
