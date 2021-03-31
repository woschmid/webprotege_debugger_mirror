package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Sets the search filter for the possibly faulty axioms to be shown.
 */
public class SetFilterAction implements ProjectAction<DebuggingSessionStateResult> {

    private ProjectId projectId;

    private boolean aBox;

    private boolean tBox;

    private boolean rBox;

    @Nullable
    private String searchString;

    @GwtSerializationConstructor
    private SetFilterAction() {}

    public SetFilterAction(@Nonnull ProjectId projectId, boolean aBox, boolean tBox, boolean rBox) {
        this(projectId, aBox, tBox, rBox, null);
    }

    public SetFilterAction(@Nonnull ProjectId projectId, boolean aBox, boolean tBox, boolean rBox, @Nullable String searchString) {
        this.projectId = projectId;
        this.aBox = aBox;
        this.tBox = tBox;
        this.rBox = rBox;
        this.searchString = searchString;
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

    public @Nullable String getSearchString() {
        return searchString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetFilterAction that = (SetFilterAction) o;
        return aBox == that.aBox && tBox == that.tBox && rBox == that.rBox && projectId.equals(that.projectId) && Objects.equals(searchString, that.searchString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, aBox, tBox, rBox, searchString);
    }

    @Override
    public String toString() {
        return toStringHelper("SetFilterAction")
                .addValue(projectId)
                .addValue(aBox)
                .addValue(tBox)
                .addValue(rBox)
                .addValue(searchString)
                .toString();

    }
}
