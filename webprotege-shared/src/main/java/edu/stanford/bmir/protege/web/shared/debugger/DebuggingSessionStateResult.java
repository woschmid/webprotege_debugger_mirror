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

    @Nullable private PossiblyFaultyAxioms possiblyFaultyAxioms;

    @Nullable private CorrectAxioms correctAxioms;

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
     * If isOk is <code>false</code> then this message contains details about the error reason in the backend.
     * If isOk is <code>true</code> then this message contains general information like consistency or other useful feedback
     * for the user from the backend (for example that repair was successful).
     */
    @Nullable private String message;

    /**
     * Applied filter for Possibly Faulty Axioms.
     */
    private boolean aBoxFilter, tBoxFilter, rBoxFilter;

    /**
     * The page index of the currently shown pages. 0 = first page. Range [0..max:pages-1]
     */
    private int index = 0;

    /**
     * Number of pages with filtered possibly faulty axioms. 0 indicates no pages at all.
     */
    private int pages = 0;

    @GwtSerializationConstructor
    private DebuggingSessionStateResult() {}

    public DebuggingSessionStateResult(@Nonnull Boolean isOk,
                                       @Nullable UserId userId,
                                       @Nullable Query query,
                                       @Nullable List<Diagnosis> diagnoses,
                                       @Nullable List<TestCase> positiveTestCases,
                                       @Nullable List<TestCase> negativeTestCases,
                                       @Nullable PossiblyFaultyAxioms possiblyFaultyAxioms,
                                       @Nullable CorrectAxioms correctAxioms,
                                       @Nullable SessionState sessionState,
                                       @Nullable String msg,
                                       boolean aBox,
                                       boolean tBox,
                                       boolean rBox,
                                       int index,
                                       int pages) {
        this.isOk = isOk;
        this.userId = userId;
        this.query = query;
        this.diagnoses = diagnoses;
        this.positiveTestCases = positiveTestCases;
        this.negativeTestCases = negativeTestCases;
        this.possiblyFaultyAxioms = possiblyFaultyAxioms;
        this.correctAxioms = correctAxioms;
        this.sessionState = sessionState;
        this.message = msg;
        this.aBoxFilter = aBox;
        this.tBoxFilter = tBox;
        this.rBoxFilter = rBox;
        this.index = index;
        this.pages = pages;
    }

    @Nonnull
    public Boolean isOk() {
        return isOk;
    }

    @Nullable
    public String getMessage() {
        return message;
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
    public PossiblyFaultyAxioms getPossiblyFaultyAxioms() {
        return possiblyFaultyAxioms;
    }

    @Nullable
    public CorrectAxioms getCorrectAxioms() {
        return correctAxioms;
    }

    @Nullable
    public SessionState getSessionState() {
        return sessionState;
    }

    public boolean isABox() {
        return aBoxFilter;
    }

    public boolean isTBox() {
        return tBoxFilter;
    }

    public boolean isRBox() {
        return rBoxFilter;
    }

    public int getIndex() {
        return index;
    }

    public int getPages() {
        return pages;
    }

}
