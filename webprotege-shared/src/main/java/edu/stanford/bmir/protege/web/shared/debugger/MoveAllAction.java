package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.ProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Moves all currently presented axiom either from the possibly faulty to the correct axioms or vice versa.
 */
public class MoveAllAction implements ProjectAction<DebuggingSessionStateResult> {

    private ProjectId projectId;

    /**
     * Flag that indicates which set is meant to be moved.
     * <br>
     * <code>true</code> means the currently shown possible faulty
     * axioms (i.e. moveDown), <code>false</code> means the correct axioms to be moved (i.e. moveUp).
     */
    private Boolean moveDown;

    /**
     * Action to indicate the movement of all currently presented axiom either from the possibly faulty to the correct
     * axioms or vice versa.
     *
     * @param projectId The project id.
     * @param moveDown <code>true</code> means the currently shown possible faulty axioms (i.e. moveDown),
     *                 <code>false</code> means the correct axioms to be moved (i.e. moveUp).
     */
    public MoveAllAction(@Nonnull ProjectId projectId, @Nonnull Boolean moveDown) {
        this.projectId = projectId;
        this.moveDown = moveDown;
    }

    @GwtSerializationConstructor
    private MoveAllAction() {}

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return null;
    }

    @Nonnull
    public Boolean isMoveDown() {
        return moveDown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveAllAction that = (MoveAllAction) o;
        return Objects.equals(projectId, that.projectId) && Objects.equals(moveDown, that.moveDown);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, moveDown);
    }

    @Override
    public String toString() {
        return toStringHelper("MoveAllAction")
                .addValue(projectId)
                .addValue(moveDown)
                .toString();
    }
}
