package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.IsSerializable;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

public class TestCase implements IsSerializable, Serializable {

    private SafeHtml axiom;

    @GwtSerializationConstructor
    public TestCase() {}

    public TestCase(@Nonnull SafeHtml a) {
        this.axiom = a;
    }

    @Nonnull
    public SafeHtml getAxiom() {
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
