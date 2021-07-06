package edu.stanford.bmir.protege.web.shared.shacl;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ValidateShaclAction implements ProjectAction<ShaclValidationResult> {

    private ProjectId projectId;

    private String shaclEditorText;

    private OWLEntity owlEntity;

    public ValidateShaclAction(ProjectId projectId, String shaclEditorText) {
        this.projectId = projectId;
        this.shaclEditorText = shaclEditorText;
    }

    public ValidateShaclAction(ProjectId projectId, String shaclEditorText, OWLEntity owlEntity) {
        this.projectId = projectId;
        this.shaclEditorText = shaclEditorText;
        this.owlEntity = owlEntity;
    }

    @GwtSerializationConstructor
    private ValidateShaclAction() {}

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    public String getShaclEditorText() {
        return shaclEditorText;
    }

    public OWLEntity getOwlEntity() {
        return owlEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidateShaclAction that = (ValidateShaclAction) o;
        return Objects.equals(projectId, that.projectId) && Objects.equals(shaclEditorText, that.shaclEditorText) && Objects.equals(owlEntity, that.owlEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, shaclEditorText, owlEntity);
    }

    @Override
    public String toString() {
        return "ValidateShaclAction{" +
                "projectId=" + projectId +
                ", shaclEditorText='" + shaclEditorText + '\'' +
                ", owlEntity=" + owlEntity +
                '}';
    }
}
