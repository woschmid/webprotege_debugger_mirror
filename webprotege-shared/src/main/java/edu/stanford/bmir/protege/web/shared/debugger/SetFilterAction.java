package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

public class SetFilterAction implements ProjectAction<DebuggingSessionStateResult> {

    private ProjectId projectId;

    private boolean aBox;

    private boolean tBox;

    private boolean rBox;

    @GwtSerializationConstructor
    private SetFilterAction() {}

    public SetFilterAction(@Nonnull ProjectId projectId, boolean aBox, boolean tBox, boolean rBox) {
        this.projectId = projectId;
        this.aBox = aBox;
        this.tBox = tBox;
        this.rBox = rBox;
    }

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    public boolean isABox() {
        return aBox;
    }

    public boolean isTBox() {
        return tBox;
    }

    public boolean isRBox() {
        return rBox;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetFilterAction that = (SetFilterAction) o;
        return aBox == that.aBox && tBox == that.tBox && rBox == that.rBox && Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, aBox, tBox, rBox);
    }

    @Override
    public String toString() {
        return toStringHelper("SetFilterAction")
                .addValue(projectId)
                .addValue(aBox)
                .addValue(tBox)
                .addValue(rBox)
                .toString();

    }
}
