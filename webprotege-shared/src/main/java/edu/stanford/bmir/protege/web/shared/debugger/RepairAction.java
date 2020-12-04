package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

public class RepairAction implements ProjectAction<DebuggingSessionStateResult> {

    private ProjectId projectId;

    private RepairDetails repairDetails;

    @Deprecated public RepairAction(@Nonnull ProjectId projectId) {
        this.projectId = checkNotNull(projectId);
    }

    public RepairAction(@Nonnull ProjectId projectId, @Nonnull RepairDetails repairDetails) {
        this.projectId = projectId;
        this.repairDetails = repairDetails;
    }

    @GwtSerializationConstructor
    private RepairAction() {}

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    @Nonnull
    public RepairDetails getRepairDetails() {
        return repairDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairAction that = (RepairAction) o;
        return Objects.equals(projectId, that.projectId) && Objects.equals(repairDetails, that.repairDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, repairDetails);
    }

    @Override
    public String toString() {
        return toStringHelper("RepairAction")
                .addValue(projectId)
                .addValue(repairDetails)
                .toString();
    }
}
