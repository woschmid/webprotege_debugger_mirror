package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.debugger.StopDebuggingAction;
import edu.stanford.bmir.protege.web.shared.debugger.StopDebuggingResult;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class StopDebuggingActionHandler extends AbstractProjectActionHandler<StopDebuggingAction, StopDebuggingResult> {

    @Inject
    public StopDebuggingActionHandler(@Nonnull AccessManager accessManager) {
        super(accessManager);
    }

    @Nonnull
    @Override
    public Class<StopDebuggingAction> getActionClass() {
        return StopDebuggingAction.class;
    }

    @Nonnull
    @Override
    public StopDebuggingResult execute(@Nonnull StopDebuggingAction action, @Nonnull ExecutionContext executionContext) {
        return new StopDebuggingResult("Hi, you pressed the Stop button!");
    }
}
