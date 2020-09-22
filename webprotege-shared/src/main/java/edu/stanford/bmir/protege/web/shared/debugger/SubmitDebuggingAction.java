package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.common.collect.ImmutableMap;
import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

public class SubmitDebuggingAction implements ProjectAction<DebuggingSessionStateResult> {
    private ProjectId projectId;
    private ImmutableMap<SafeHtml, Boolean> answers;

    public SubmitDebuggingAction(@Nonnull ProjectId projectId,
                                 @Nonnull ImmutableMap<SafeHtml, Boolean> answers
                                 ) {
        this.projectId = checkNotNull(projectId);
        this.answers = checkNotNull(answers);
    }

    @GwtSerializationConstructor
    private SubmitDebuggingAction() {
    }

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    @Nonnull
    public ImmutableMap<SafeHtml, Boolean> getAnswers() {
        return answers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmitDebuggingAction that = (SubmitDebuggingAction) o;
        return projectId.equals(that.projectId) &&
                answers.equals(that.answers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, answers);
    }

    @Override
    public String toString() {
        return toStringHelper("SubmitDebuggingAction")
                .addValue(projectId)
                .addValue(answers)
                .toString();
    }
}
