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
     * The flag indicates which page shall be navigated.
     * <code>true</code> indicates the possibly faulty axioms.
     * <code>false</code> indicates the correct axioms.
     */
    private Boolean pageFlag = Boolean.TRUE;

    /**
     * The page when paginating through the shown possibly faulty and correct axioms.
     * Any numbers are possible - positive, zero and negative.
     * The paging will be checked serverside.
     */
    private int page;

    /**
     *
     * @param projectId The current project's id.
     * @param page The page to be shown. Any positive or negative number is allowed.
     *             The paging will be checked serverside.
     */
    @Deprecated
    public PaginationAction(ProjectId projectId, int page) {
        this.projectId = checkNotNull(projectId);
        this.page = page;
    }

    /**
     *
     * @param projectId The current project's id.
     * @param pageFlag The flag indicates which page is meant to be navigated.
     *                 <code>true</code> indicates the possibly faulty axioms.
     *                 <code>false</code> indicates the correct axioms.
     * @param page The page to be shown. Any positive or negative number is allowed.
     *             The paging will be checked serverside.
     */
    public PaginationAction(ProjectId projectId, Boolean pageFlag, int page) {
        this.projectId = checkNotNull(projectId);
        this.pageFlag = checkNotNull(pageFlag);
        this.page = page;
    }

    @GwtSerializationConstructor
    public PaginationAction(){}

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    public int getPage() {
        return page;
    }

    @Nonnull
    public Boolean getPageFlag() {
        return pageFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaginationAction that = (PaginationAction) o;
        return page == that.page && Objects.equals(projectId, that.projectId) && Objects.equals(pageFlag, that.pageFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, pageFlag, page);
    }

    @Override
    public String toString() {
        return toStringHelper("PaginationAction")
                .addValue(projectId)
                .addValue(pageFlag)
                .addValue(page)
                .toString();
    }
}
