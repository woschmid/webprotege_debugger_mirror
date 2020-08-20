package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingResult;
import edu.stanford.bmir.protege.web.shared.debugger.StopDebuggingAction;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class StopDebuggingActionHandler extends AbstractProjectActionHandler<StopDebuggingAction, DebuggingResult> {

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
    public DebuggingResult execute(@Nonnull StopDebuggingAction action, @Nonnull ExecutionContext executionContext) {
        return new DebuggingResult(null, null, null, null);
    }
}
