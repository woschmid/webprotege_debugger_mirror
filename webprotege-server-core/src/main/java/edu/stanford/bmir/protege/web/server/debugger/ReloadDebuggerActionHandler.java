package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggerStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.ReloadDebuggerAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class ReloadDebuggerActionHandler extends AbstractProjectActionHandler<ReloadDebuggerAction, DebuggerStateResult> {

    @Nonnull
    private final ProjectId projectId;

    @Nonnull
    private final DebuggingSessionManager debuggingSessionManager;


    @Inject
    public ReloadDebuggerActionHandler(@Nonnull ProjectId projectId,
                                       @Nonnull AccessManager accessManager,
                                       @Nonnull DebuggingSessionManager debuggingSessionManager
                                        ) {
        super(accessManager);
        this.projectId = checkNotNull(projectId);
        this.debuggingSessionManager = checkNotNull(debuggingSessionManager);
    }

    @Nonnull
    @Override
    public Class<ReloadDebuggerAction> getActionClass() {
        return ReloadDebuggerAction.class;
    }

    @Nonnull
    @Override
    public DebuggerStateResult execute(@Nonnull ReloadDebuggerAction action, @Nonnull ExecutionContext executionContext) {
        return debuggingSessionManager.getDebuggingState(projectId);
    }

    @Nullable
    @Override
    protected BuiltInAction getRequiredExecutableBuiltInAction() {
        return BuiltInAction.DEBUG_ONTOLOGY;
    }
}