package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An action to paginate through the currently shown possibly faulty axioms.
 */
public class PaginationAction implements ProjectAction<DebuggingSessionStateResult> {

    private ProjectId projectId;

    /**
     * The step when paginating through the shown possibly faulty axioms.
     * Any numbers are possible - positive, zero and negative.
     * The paging will be checked serverside.
     * A step size of -1 means navigating one page back, and a size of 1 means navigating one page forward.
     */
    private int step;

    /**
     *
     * @param projectId The current project's id.
     * @param step The step size. Any positive or negative number is allowed. A value of <code>-1</code> indicates
     *             navigating backwards by one page, a value of <code>1</code> indicates a navigation forward by one page.
     */
    public PaginationAction(ProjectId projectId, int step) {
        this.projectId = checkNotNull(projectId);
        this.step = step;
    }
    @GwtSerializationConstructor
    public PaginationAction(){}

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    public int getStep() {
        return step;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaginationAction that = (PaginationAction) o;
        return step == that.step && Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, step);
    }

    @Override
    public String toString() {
        return toStringHelper("PaginationAction")
                .addValue(projectId)
                .addValue(step)
                .toString();
    }
}
