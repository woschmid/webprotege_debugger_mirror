package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Objects;

public class MoveToAction implements ProjectAction<DebuggingSessionStateResult> {
    private ProjectId projectId;
    private SafeHtml axiom;

    public MoveToAction(@Nonnull ProjectId projectId, @Nonnull SafeHtml axiom) {
        this.projectId = projectId;
        this.axiom = axiom;
    }

    @GwtSerializationConstructor
    private MoveToAction() {
    }

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    @Nonnull
    public SafeHtml getAxiom() {
        return axiom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveToAction that = (MoveToAction) o;
        return Objects.equals(projectId, that.projectId) &&
                Objects.equals(axiom, that.axiom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, axiom);
    }

    @Override
    public String toString() {
        return "MoveToAction{" +
                "projectId=" + projectId +
                ", axiom=" + axiom +
                '}';
    }
}
