package edu.stanford.bmir.protege.web.shared.shacl;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class SaveShaclAction implements ProjectAction<ShaclValidationResult> {

    private ProjectId projectId;

    private String shaclEditorText;

    @Nullable private OWLEntity owlEntity;

    public SaveShaclAction(ProjectId projectId, String shaclEditorText) {
        this(projectId, shaclEditorText, null);
    }

    public SaveShaclAction(ProjectId projectId, String shaclEditorText, @Nullable OWLEntity owlEntity) {
        this.projectId = projectId;
        this.shaclEditorText = shaclEditorText;
        this.owlEntity = owlEntity;
    }

    @GwtSerializationConstructor
    private SaveShaclAction() {}

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    public String getShaclEditorText() {
        return shaclEditorText;
    }

    @Nullable
    public OWLEntity getOwlEntity() {
        return owlEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaveShaclAction that = (SaveShaclAction) o;
        return Objects.equals(projectId, that.projectId) && Objects.equals(shaclEditorText, that.shaclEditorText) && Objects.equals(owlEntity, that.owlEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, shaclEditorText, owlEntity);
    }

    @Override
    public String toString() {
        return "SaveShaclAction{" +
                "projectId=" + projectId +
                ", shaclEditorText='" + shaclEditorText + '\'' +
                ", owlEntity=" + owlEntity +
                '}';
    }
}
