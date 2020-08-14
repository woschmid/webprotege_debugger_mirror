package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.debugger.StartDebuggingAction;
import edu.stanford.bmir.protege.web.shared.debugger.StartDebuggingResult;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class StartDebuggingActionHandler extends AbstractProjectActionHandler<StartDebuggingAction, StartDebuggingResult> {

    @Inject
    public StartDebuggingActionHandler(@Nonnull AccessManager accessManager) {
        super(accessManager);
    }

    @Nonnull
    @Override
    public Class<StartDebuggingAction> getActionClass() {
        return StartDebuggingAction.class;
    }

    @Nonnull
    @Override
    public StartDebuggingResult execute(@Nonnull StartDebuggingAction action, @Nonnull ExecutionContext executionContext) {
        return new StartDebuggingResult("Hi, you pressed the Start button!");
    }
}
