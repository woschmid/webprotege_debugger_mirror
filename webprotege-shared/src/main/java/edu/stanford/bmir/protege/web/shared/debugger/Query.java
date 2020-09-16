package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.IsSerializable;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Query implements IsSerializable {

    private Set<SafeHtml> axioms;

    @GwtSerializationConstructor
    private Query() {}

    public Query(@Nonnull Set<SafeHtml> axioms) {
        this.axioms = new HashSet<>(axioms);
    }

    @Nonnull
    public Set<SafeHtml> getAxioms() {
        return axioms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Query query = (Query) o;
        return axioms.equals(query.axioms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(axioms);
    }

    @Override
    public String toString() {
        return "Query{" +
                "axioms=" + axioms +
                '}';
    }

}
