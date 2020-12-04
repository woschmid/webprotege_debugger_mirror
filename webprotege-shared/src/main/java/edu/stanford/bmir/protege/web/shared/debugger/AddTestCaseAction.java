package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

public class AddTestCaseAction implements ProjectAction<DebuggingSessionStateResult>  {

    private ProjectId projectId;

    private String testCase;

    private boolean isEntailed;

    public AddTestCaseAction(@Nonnull ProjectId projectId, @Nonnull String axiom, boolean isEntailed) {
        this.projectId = checkNotNull(projectId);
        this.testCase = axiom;
        this.isEntailed = isEntailed;
    }

    @GwtSerializationConstructor
    private AddTestCaseAction() {}

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    @Nonnull
    public String getTestCase() {
        return testCase;
    }

    public boolean isEntailed() {
        return isEntailed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddTestCaseAction that = (AddTestCaseAction) o;
        return isEntailed == that.isEntailed && Objects.equals(projectId, that.projectId) && Objects.equals(testCase, that.testCase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, testCase, isEntailed);
    }

    @Override
    public String toString() {
        return toStringHelper("AddTestCaseAction")
                .addValue(projectId)
                .addValue(testCase)
                .addValue(isEntailed)
                .toString();
    }
}
