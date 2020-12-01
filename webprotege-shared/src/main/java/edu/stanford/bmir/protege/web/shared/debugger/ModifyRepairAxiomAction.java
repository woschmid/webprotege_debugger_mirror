package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

public class ModifyRepairAxiomAction implements ProjectAction<DebuggingSessionStateResult>  {

    private ProjectId projectId;

    private SafeHtml originalAxiom;

    private SafeHtml modifiedAxiom;

    @GwtSerializationConstructor
    private ModifyRepairAxiomAction() {}

    public ModifyRepairAxiomAction(@Nonnull ProjectId projectId, @Nonnull SafeHtml originalAxiom, @Nonnull SafeHtml modifiedAxiom) {
        this.projectId = projectId;
        this.originalAxiom = originalAxiom;
        this.modifiedAxiom = modifiedAxiom;
    }

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    @Nonnull
    public SafeHtml getOriginalAxiom() {
        return originalAxiom;
    }

    @Nonnull
    public SafeHtml getModifiedAxiom() {
        return modifiedAxiom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModifyRepairAxiomAction that = (ModifyRepairAxiomAction) o;
        return Objects.equals(projectId, that.projectId) &&
                Objects.equals(originalAxiom, that.originalAxiom) &&
                Objects.equals(modifiedAxiom, that.modifiedAxiom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, originalAxiom, modifiedAxiom);
    }

    @Override
    public String toString() {
        return toStringHelper("ModifyRepairAxiomAction")
                .addValue(projectId)
                .addValue(originalAxiom)
                .addValue(modifiedAxiom)
                .toString();
    }

}
