package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Objects;

public class RepairAction implements ProjectAction<DebuggingSessionStateResult> {

    private ProjectId projectId;

    private ImmutableMap<SafeHtml, String> axiomsToModify;

    private ImmutableSet<SafeHtml> axiomsToDelete;

    private Integer index;

    @Deprecated
    public RepairAction(@Nonnull ProjectId projectId,
                        @Nonnull ImmutableMap<SafeHtml, String> axiomsToModify,
                        @Nonnull ImmutableSet<SafeHtml> axiomsToDelete) {
        this.projectId = projectId;
        this.axiomsToModify = axiomsToModify;
        this.axiomsToDelete = axiomsToDelete;
        this.index = 0;
    }

    public RepairAction(@Nonnull ProjectId projectId,
                        @Nonnull ImmutableMap<SafeHtml, String> axiomsToModify,
                        @Nonnull ImmutableSet<SafeHtml> axiomsToDelete,
                        @Nonnull Integer diagnosisIndex) {
        this.projectId = projectId;
        this.axiomsToModify = axiomsToModify;
        this.axiomsToDelete = axiomsToDelete;
        this.index = diagnosisIndex;
    }

    @GwtSerializationConstructor
    private RepairAction() {}

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    @Nonnull
    public ImmutableMap<SafeHtml, String> getAxiomsToModify() {
        return axiomsToModify;
    }

    @Nonnull
    public ImmutableSet<SafeHtml> getAxiomsToDelete() {
        return axiomsToDelete;
    }

    @Nonnull
    public Integer getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairAction that = (RepairAction) o;
        return Objects.equals(projectId, that.projectId) && Objects.equals(axiomsToModify, that.axiomsToModify) && Objects.equals(axiomsToDelete, that.axiomsToDelete) && Objects.equals(index, that.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, axiomsToModify, axiomsToDelete, index);
    }

}
