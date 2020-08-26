package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;

import javax.annotation.Nullable;
import java.util.List;

public class DebuggingResult implements Result {

    @Nullable
    private Query query;

    @Nullable
    private List<Diagnosis> diagnoses;

    @Nullable
    private List<TestCase> positiveTestCases;

    @Nullable
    private List<TestCase> negativeTestCases;

    @GwtSerializationConstructor
    private DebuggingResult() {}

    public DebuggingResult(@Nullable Query query, @Nullable List<Diagnosis> diagnoses, @Nullable List<TestCase> positiveTestCases, @Nullable List<TestCase> negativeTestCases) {
        this.query = query;
        this.diagnoses = diagnoses;
        this.positiveTestCases = positiveTestCases;
        this.negativeTestCases = negativeTestCases;
    }

    @Nullable
    public Query getQuery() {
        return query;
    }

    @Nullable
    public List<Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    @Nullable
    public List<TestCase> getPositiveTestCases() {
        return positiveTestCases;
    }

    @Nullable
    public List<TestCase> getNegativeTestCases() {
        return negativeTestCases;
    }
}
