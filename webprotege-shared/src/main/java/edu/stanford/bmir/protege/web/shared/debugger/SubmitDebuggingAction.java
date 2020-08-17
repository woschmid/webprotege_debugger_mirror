package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

public class SubmitDebuggingAction implements ProjectAction<SubmitDebuggingResult> {
    private ProjectId projectId;

    public SubmitDebuggingAction(@Nonnull ProjectId projectId) {
        this.projectId = checkNotNull(projectId);
    }

    @GwtSerializationConstructor
    private SubmitDebuggingAction() {
    }

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
        SubmitDebuggingAction other = (SubmitDebuggingAction) obj;
        return this.projectId.equals(other.projectId);
    }

    @Override
    public String toString() {
        return toStringHelper("StartDebuggingAction")
                .addValue(projectId)
                .toString();
    }
}
