package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.ReloadDebuggerAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class ReloadDebuggerActionHandler extends AbstractProjectActionHandler<ReloadDebuggerAction, DebuggingSessionStateResult> {

    @Nonnull
    private final DebuggingSession session;

    @Inject
    public ReloadDebuggerActionHandler(@Nonnull DebuggingSession debuggingSession,
                                       @Nonnull AccessManager accessManager) {
        super(accessManager);
        this.session = checkNotNull(debuggingSession);
    }

    @Nonnull
    @Override
    public Class<ReloadDebuggerAction> getActionClass() {
        return ReloadDebuggerAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull ReloadDebuggerAction action, @Nonnull ExecutionContext executionContext) {
        return DebuggingResultFactory.generateResult(session, Boolean.TRUE, null);
    }

    @Nullable
    @Override
    protected BuiltInAction getRequiredExecutableBuiltInAction() {
        return BuiltInAction.DEBUG_ONTOLOGY;
    }
}