package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingResult;
import edu.stanford.bmir.protege.web.shared.debugger.SubmitDebuggingAction;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class SubmitDebuggingActionHandler extends AbstractProjectActionHandler<SubmitDebuggingAction, DebuggingResult> {

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
    public DebuggingResult execute(@Nonnull SubmitDebuggingAction action, @Nonnull ExecutionContext executionContext) {
        return new DebuggingResult(null,null,null,null);
    }
}
