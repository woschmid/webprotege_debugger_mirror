package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.user.UserId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class DebuggingSessionStateResult implements Result {

    /**
     * Represents the owner of a debugging session.
     * If <code>null</code> then there exists no debugging session for this project.
     */
    @Nullable private UserId userId;

    /**
     * Represents a query to be shown. Do not show if <code>null</code>.
     */
    @Nullable private Query query;

    /**
     * Represents diagnoses to be shown. Do not show if <code>null</code>.
     */
    @Nullable private List<Diagnosis> diagnoses;

    /**
     * Represents positive test cases to be shown in frontend. Do not show if <code>null</code>.
     */
    @Nullable private List<TestCase> positiveTestCases;

    /**
     * Represents negative test cases to be shown in frontend. Do not show if <code>null</code>.
     */
    @Nullable private List<TestCase> negativeTestCases;

    /**
     * Represents the current state of a debugging session of this project.
     * If state is <code>null</code> then there exists no debugging session for this project.
     */
    @Nullable private SessionState sessionState;

    /**
     * Flag for errors in the backend. If isOk is <code>true</code> then the backend is in an OK state, otherwise
     * an exception has occurred and the errorMessage should be not null.
     */
    @Nonnull private Boolean isOk = Boolean.TRUE;

    /**
     * If isOk is <code>false</code> then this errorMessage contains details about the error reason in the backend.
     */
    @Nullable private String errorMessage;

    @GwtSerializationConstructor
    private DebuggingSessionStateResult() {}

    public DebuggingSessionStateResult(@Nonnull Boolean isOk, @Nullable UserId userId, @Nullable Query query, @Nullable List<Diagnosis> diagnoses, @Nullable List<TestCase> positiveTestCases, @Nullable List<TestCase> negativeTestCases, @Nullable SessionState sessionState, @Nullable String errorMsg) {
        this.isOk = isOk;
        this.userId = userId;
        this.query = query;
        this.diagnoses = diagnoses;
        this.positiveTestCases = positiveTestCases;
        this.negativeTestCases = negativeTestCases;
        this.sessionState = sessionState;
        this.errorMessage = errorMsg;
    }

    @Nonnull
    public Boolean isOk() {
        return isOk;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    @Nullable
    public UserId getUserId() {
        return userId;
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

    @Nullable
    public SessionState getSessionState() {
        return sessionState;
    }
}
