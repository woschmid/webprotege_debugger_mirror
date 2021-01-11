package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.IsSerializable;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CorrectAxioms implements IsSerializable {

    private List<SafeHtml> axioms;

    @GwtSerializationConstructor
    private CorrectAxioms() {}

    public CorrectAxioms(@Nonnull List<SafeHtml> axioms) {
        this.axioms = new ArrayList<>(axioms);
    }

    public List<SafeHtml> getAxioms() {
        return axioms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CorrectAxioms that = (CorrectAxioms) o;
        return Objects.equals(axioms, that.axioms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(axioms);
    }

    @Override
    public String toString() {
        return "CorrectAxioms{" +
                "axioms=" + axioms +
                '}';
    }
}
