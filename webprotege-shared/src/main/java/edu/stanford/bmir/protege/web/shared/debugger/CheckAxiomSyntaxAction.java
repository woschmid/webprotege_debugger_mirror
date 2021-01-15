package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CheckAxiomSyntaxAction implements ProjectAction<DebuggingSessionStateResult> {

    @Nonnull private ProjectId projectId;

    @Nonnull private String axiom;

    public CheckAxiomSyntaxAction(@Nonnull ProjectId projectId, @Nonnull String axiom) {
        this.projectId = projectId;
        this.axiom = axiom;
    }

    @GwtSerializationConstructor
    private CheckAxiomSyntaxAction() {}

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    @Nonnull
    public String getAxiom() {
        return axiom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckAxiomSyntaxAction that = (CheckAxiomSyntaxAction) o;
        return projectId.equals(that.projectId) && axiom.equals(that.axiom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, axiom);
    }
}
