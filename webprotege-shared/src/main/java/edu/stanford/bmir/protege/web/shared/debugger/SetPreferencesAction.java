package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

public class SetPreferencesAction implements ProjectAction<DebuggingSessionStateResult>  {

    private ProjectId projectId;

    private Preferences preferences;

    @GwtSerializationConstructor
    private SetPreferencesAction() {}

    public SetPreferencesAction(@Nonnull ProjectId projectId, @Nonnull Preferences preferences) {
        this.projectId = projectId;
        this.preferences = preferences;
    }

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    @Nonnull
    public Preferences getPreferences() {
        return preferences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetPreferencesAction that = (SetPreferencesAction) o;
        return Objects.equals(projectId, that.projectId) && Objects.equals(preferences, that.preferences);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, preferences);
    }

    @Override
    public String toString() {
        return toStringHelper("SetPreferencesAction")
                .addValue(projectId)
                .addValue(preferences)
                .toString();
    }
}
