package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.gwt.user.client.rpc.IsSerializable;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Diagnosis implements IsSerializable {

    private Set<String> axioms;

    @GwtSerializationConstructor
    private Diagnosis() {}

    public Diagnosis(@Nonnull Set<String> axioms) {
        this.axioms = new HashSet<>(axioms);
    }

    @Nonnull
    public Set<String> getAxioms() {
        return axioms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Diagnosis diagnosis = (Diagnosis) o;
        return Objects.equals(axioms, diagnosis.axioms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(axioms);
    }

    @Override
    public String toString() {
        return "Diagnosis{" +
                "axioms=" + axioms +
                '}';
    }

}
