package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

public class RemoveTestCaseAction implements ProjectAction<DebuggingSessionStateResult>  {

    private ProjectId projectId;

    private SafeHtml testCase;

    public RemoveTestCaseAction(@Nonnull ProjectId projectId, @Nonnull SafeHtml axiom) {
        this.projectId = checkNotNull(projectId);
        this.testCase = axiom;
    }

    @GwtSerializationConstructor
    private RemoveTestCaseAction() {}

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    @Nonnull
    public SafeHtml getTestCase() {
        return testCase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoveTestCaseAction that = (RemoveTestCaseAction) o;
        return Objects.equals(projectId, that.projectId) &&
                Objects.equals(testCase, that.testCase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, testCase);
    }

    @Override
    public String toString() {
        return toStringHelper("RemoveTestCaseAction")
                .addValue(projectId)
                .addValue(testCase)
                .toString();
    }
}
