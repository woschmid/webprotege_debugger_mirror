package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

public class StartDebuggingAction implements ProjectAction<DebuggingSessionStateResult> {

    private ProjectId projectId;

    public StartDebuggingAction(@Nonnull ProjectId projectId) {
        this.projectId = checkNotNull(projectId);
    }

    @GwtSerializationConstructor
    private StartDebuggingAction() {}

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    @Override
    public int hashCode() {
        return projectId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StartDebuggingAction)) {
            return false;
        }
        StartDebuggingAction other = (StartDebuggingAction) obj;
        return this.projectId.equals(other.projectId);
    }

    @Override
    public String toString() {
        return toStringHelper("StartDebuggingAction")
                .addValue(projectId)
                .toString();
    }
}
