package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Identify the OWLEntity behind a diagnosis axiom represented as SafeHtml.
 */
public class GetEntityAction implements ProjectAction<GetEntityResult> {

    private ProjectId projectId;

    private SafeHtml axiom;

    @GwtSerializationConstructor
    private GetEntityAction() {}

    public GetEntityAction(@Nonnull ProjectId projectId, @Nonnull SafeHtml diagnosisAxiom) {
        this.projectId = checkNotNull(projectId);
        this.axiom = checkNotNull(diagnosisAxiom);
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
        GetEntityAction that = (GetEntityAction) o;
        return projectId.equals(that.projectId) &&
                axiom.equals(that.axiom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, axiom);
    }

    @Override
    public String toString() {
        return toStringHelper("GetEntityAction")
                .addValue(projectId)
                .addValue(axiom)
                .toString();
    }
}
