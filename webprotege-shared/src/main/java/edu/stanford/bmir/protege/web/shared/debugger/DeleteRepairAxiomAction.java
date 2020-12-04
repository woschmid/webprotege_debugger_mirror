package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

@Deprecated public class DeleteRepairAxiomAction implements ProjectAction<DebuggingSessionStateResult> {

    private ProjectId projectId;

    private SafeHtml axiomToDelete;

    @GwtSerializationConstructor
    private DeleteRepairAxiomAction() {}

    public DeleteRepairAxiomAction(@Nonnull ProjectId projectId, @Nonnull SafeHtml axiomToDelete) {
        this.projectId = projectId;
        this.axiomToDelete = axiomToDelete;
    }

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    @Nonnull
    public SafeHtml getAxiomToDelete() {
        return axiomToDelete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteRepairAxiomAction that = (DeleteRepairAxiomAction) o;
        return Objects.equals(projectId, that.projectId) &&
                Objects.equals(axiomToDelete, that.axiomToDelete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, axiomToDelete);
    }

    @Override
    public String toString() {
        return toStringHelper("DeleteRepairAxiomAction")
                .addValue(projectId)
                .addValue(axiomToDelete)
                .toString();
    }
}
