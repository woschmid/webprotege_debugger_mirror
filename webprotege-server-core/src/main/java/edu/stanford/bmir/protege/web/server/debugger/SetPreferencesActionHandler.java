package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.SetPreferencesAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class SetPreferencesActionHandler extends AbstractProjectActionHandler<SetPreferencesAction, DebuggingSessionStateResult>  {

    @Nonnull
    private final DebuggingSession session;

    @Inject
    public SetPreferencesActionHandler(@Nonnull AccessManager accessManager, @Nonnull DebuggingSession session) {
        super(accessManager);
        this.session = session;
    }

    @Nonnull
    @Override
    public Class<SetPreferencesAction> getActionClass() {
        return SetPreferencesAction.class;
    }

    @Nonnull
    @Override
    public DebuggingSessionStateResult execute(@Nonnull SetPreferencesAction action, @Nonnull ExecutionContext executionContext) {
        try {
            return session.setPreferences(executionContext.getUserId(), action.getPreferences());
        } catch (ConcurrentUserException e) {
            Util.logException(getActionClass(), e);
            return DebuggingResultFactory.generateResult(session, Boolean.FALSE, e.getMessage());
        }
    }

    @Nullable
    @Override
    protected BuiltInAction getRequiredExecutableBuiltInAction() {
        return BuiltInAction.DEBUG_ONTOLOGY;
    }
}
