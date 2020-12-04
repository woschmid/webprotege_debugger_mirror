package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.IsSerializable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Detailed information about the repair action containing axioms to be deleted and axioms to be modified in order to
 *
 */
public class RepairDetails implements IsSerializable {

    /**
     * Contains a set of axioms from a repair to be removed from the ontology, diagnosis model and diagnosis.
     */
    @Nonnull private Set<SafeHtml> axiomsToDelete;

    /**
     * Contains a map of axioms from the diagnosis to modify.
     * The keys in the map represent the SafeHtml representations of the old axioms, their values are the string
     * representation of the new modified axiom.
     */
    @Nonnull private Map<SafeHtml, String> axiomsToModify;

    public RepairDetails(@Nonnull Set<SafeHtml> axiomsToDelete, @Nonnull Map<SafeHtml, String> axiomsToModify) {
        this.axiomsToDelete = axiomsToDelete;
        this.axiomsToModify = axiomsToModify;
    }

    @Nonnull
    public Set<SafeHtml> getAxiomsToDelete() {
        return axiomsToDelete;
    }

    @Nonnull
    public Map<SafeHtml, String> getAxiomsToModify() {
        return axiomsToModify;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairDetails that = (RepairDetails) o;
        return axiomsToDelete.equals(that.axiomsToDelete) && axiomsToModify.equals(that.axiomsToModify);
    }

    @Override
    public int hashCode() {
        return Objects.hash(axiomsToDelete, axiomsToModify);
    }

    @Override
    public String toString() {
        return toStringHelper("RepairDetails")
                .addValue(axiomsToDelete)
                .addValue(axiomsToModify)
                .toString();
    }
}
