package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.IsSerializable;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class Diagnosis implements IsSerializable {

    private List<SafeHtml> axioms;

    @GwtSerializationConstructor
    private Diagnosis() {}

    public Diagnosis(@Nonnull List<SafeHtml> axioms) {
        this.axioms = axioms;
    }

    @Nonnull
    public List<SafeHtml> getAxioms() {
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
