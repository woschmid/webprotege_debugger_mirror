package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.gwt.user.client.rpc.IsSerializable;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

public class TestCase implements IsSerializable, Serializable {

    private String axiom;

    @GwtSerializationConstructor
    public TestCase() {}

    public TestCase(@Nonnull String a) {
        this.axiom = a;
    }

    @Nonnull
    public String getAxiom() {
        return axiom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestCase testCase = (TestCase) o;
        return Objects.equals(axiom, testCase.axiom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(axiom);
    }

    @Override
    public String toString() {
        return "TestCase{" +
                "axiom=" + axiom +
                '}';
    }
}
