package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class DebuggingResult implements Result {

    @Nullable
    private Query query;

    @Nullable
    private List<Diagnosis> diagnoses;

    @GwtSerializationConstructor
    private DebuggingResult() {}

    public DebuggingResult(@Nullable Query query, @Nullable List<Diagnosis> diagnoses) {
        this.query = query;
        this.diagnoses = diagnoses;
    }

    public DebuggingResult(@Nonnull Query query) {
        this.query = query;
        this.diagnoses = null;
    }

    public DebuggingResult(@Nonnull List<Diagnosis> diagnoses) {
        this.diagnoses = diagnoses;
        this.query = null;
    }

    @Nullable
    public Query getQuery() {
        return query;
    }

    @Nullable
    public List<Diagnosis> getDiagnoses() {
        return diagnoses;
    }
}
