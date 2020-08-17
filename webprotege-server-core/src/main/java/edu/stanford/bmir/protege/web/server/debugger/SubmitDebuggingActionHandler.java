package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.debugger.SubmitDebuggingAction;
import edu.stanford.bmir.protege.web.shared.debugger.SubmitDebuggingResult;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class SubmitDebuggingActionHandler extends AbstractProjectActionHandler<SubmitDebuggingAction, SubmitDebuggingResult> {

    @Inject
    public SubmitDebuggingActionHandler(@Nonnull AccessManager accessManager) {
        super(accessManager);
    }

    @Nonnull
    @Override
    public Class<SubmitDebuggingAction> getActionClass() {
        return SubmitDebuggingAction.class;
    }

    @Nonnull
    @Override
    public SubmitDebuggingResult execute(@Nonnull SubmitDebuggingAction action, @Nonnull ExecutionContext executionContext) {
        return new SubmitDebuggingResult("Hi, you pressed the Submit button!");
    }
}
