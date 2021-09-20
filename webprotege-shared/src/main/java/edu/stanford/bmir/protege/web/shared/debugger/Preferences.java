package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.gwt.user.client.rpc.IsSerializable;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Preferences implements Serializable, IsSerializable {

    /**
     * Keep alive time span in milliseconds of a debugging session from it's last activity.
     * If this time span is exceeded the project (and it's debugging session) can be purged.
     */
    private Long SESSION_KEEP_ALIVE_IN_MILLIS;

    /**
     * Sets the reasonerId timeout to 90% of SESSION_KEEP_ALIVE_IN_MILLIS;
     */
    private Long REASONER_TIMEOUT_IN_MILLIS;

    /**
     * The number of maximal shown visible possibly faulty axioms
     */
    private int MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS;

    /**
     * The number of maximal shown visible correct axioms
     */
    private int MAX_VISIBLE_CORRECT_AXIOMS;

    /**
     * The id string of the currently used reasoner.
     */
    private String reasonerId;

    /**
     * The max number of Diagnoses. 0 means computation of all diagnoses.
     */
    private int maxNumberOfDiagnoses;

    @GwtSerializationConstructor
    private Preferences() { reset(); }

    /**
     *
     * @param sessionKeepAliveInMillis
     * @param reasonerTimeoutInMillis
     * @param maxVisiblePossiblyFaultyAxioms
     * @param maxVisibleCorrectAxioms
     * @param reasonerId
     * @param maxNumberOfDiagnoses
     * @throws IllegalArgumentException
     */
    public Preferences(Long sessionKeepAliveInMillis,
                       Long reasonerTimeoutInMillis,
                       int maxVisiblePossiblyFaultyAxioms,
                       int maxVisibleCorrectAxioms,
                       String reasonerId,
                       int maxNumberOfDiagnoses) throws IllegalArgumentException {

        validate(sessionKeepAliveInMillis, reasonerTimeoutInMillis, maxVisiblePossiblyFaultyAxioms, maxVisibleCorrectAxioms, reasonerId, maxNumberOfDiagnoses);

        this.SESSION_KEEP_ALIVE_IN_MILLIS = sessionKeepAliveInMillis;
        this.REASONER_TIMEOUT_IN_MILLIS = reasonerTimeoutInMillis;
        this.MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS = maxVisiblePossiblyFaultyAxioms;
        this.MAX_VISIBLE_CORRECT_AXIOMS = maxVisibleCorrectAxioms;
        this.reasonerId = reasonerId;
        this.maxNumberOfDiagnoses = maxNumberOfDiagnoses;
    }

    public Long getSessionKeepAliveInMillis() {
        return SESSION_KEEP_ALIVE_IN_MILLIS;
    }

    public Long getReasonerTimeoutInMillis() {
        return REASONER_TIMEOUT_IN_MILLIS;
    }

    public int getMaxVisiblePossiblyFaultyAxioms() {
        return MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS;
    }

    public int getMaxVisibleCorrectAxioms() {
        return MAX_VISIBLE_CORRECT_AXIOMS;
    }

    public String getReasonerId() {
        return reasonerId;
    }

    public String[] getReasoners() {
        return new String[] {
                DefaultValues.REASONER_ID_HERMIT,
                /*DefaultValues.REASONER_ID_PELLET,*/
                DefaultValues.REASONER_ID_JFACT,
                DefaultValues.REASONER_ID_JCEL,
                DefaultValues.REASONER_ID_SNOROCKET,
                DefaultValues.REASONER_ID_ELK
        };
    }

    public int getMaxNumberOfDiagnoses() {
        return maxNumberOfDiagnoses;
    }

    /**
     * Resets the preference settings to it's default settings.
     */
    public void reset() {
        validate(DefaultValues.SESSION_KEEP_ALIVE_IN_MILLIS,
                DefaultValues.REASONER_TIMEOUT_IN_MILLIS,
                DefaultValues.MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS,
                DefaultValues.MAX_VISIBLE_CORRECT_AXIOMS,
                DefaultValues.reasonerId,
                DefaultValues.maxNumberOfDiagnoses);
        this.SESSION_KEEP_ALIVE_IN_MILLIS = DefaultValues.SESSION_KEEP_ALIVE_IN_MILLIS;
        this.REASONER_TIMEOUT_IN_MILLIS = DefaultValues.REASONER_TIMEOUT_IN_MILLIS;
        this.MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS = DefaultValues.MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS;
        this.MAX_VISIBLE_CORRECT_AXIOMS = DefaultValues.MAX_VISIBLE_CORRECT_AXIOMS;
        this.reasonerId = DefaultValues.reasonerId;
        this.maxNumberOfDiagnoses = DefaultValues.maxNumberOfDiagnoses;
    }

    /**
     * Input validation for the preference settings.
     *
     * @param sessionKeepAliveInMillis
     * @param reasonerTimeoutInMillis
     * @param maxVisiblePossiblyFaultyAxioms
     * @param maxVisibleCorrectAxioms
     * @param reasonerId
     * @param maxNumberOfDiagnoses
     * @throws IllegalArgumentException
     */
    private void validate(Long sessionKeepAliveInMillis,
                        Long reasonerTimeoutInMillis,
                        int maxVisiblePossiblyFaultyAxioms,
                        int maxVisibleCorrectAxioms,
                        String reasonerId,
                        int maxNumberOfDiagnoses) throws IllegalArgumentException {
        if (! (sessionKeepAliveInMillis >= 1000L * 60L && sessionKeepAliveInMillis <= 1000L * 60L * 60L + 6L) )
            throw new IllegalArgumentException("Values for SESSION_KEEP_ALIVE_IN_MILLIS are only allowed within a range of [" + (1000L * 60L) + ',' + (1000L * 60L * 60L + 6L) + "]");

        if (! (reasonerTimeoutInMillis >= 1000L * 60L &&  reasonerTimeoutInMillis <= sessionKeepAliveInMillis))
            throw new IllegalArgumentException("Values for REASONER_TIMEOUT_IN_MILLIS are only allowed within a range of " + (1000L * 60L) + " and SESSION_KEEP_ALIVE_IN_MILLIS");

        if (! (maxVisiblePossiblyFaultyAxioms > 0 && maxVisiblePossiblyFaultyAxioms <= 50))
            throw new IllegalArgumentException("Values for MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS are only allowed within a range of [1..50]");

        if (! (maxVisibleCorrectAxioms > 0 && maxVisibleCorrectAxioms <= 50))
            throw new IllegalArgumentException("Values for MAX_VISIBLE_CORRECT_AXIOMS are only allowed within a range of [1..50]");

        if (!Arrays.asList(getReasoners()).contains(reasonerId))
            throw new IllegalArgumentException("The reasoner '" + reasonerId + "' is not supported!");

        if (maxNumberOfDiagnoses < 0)
            throw new IllegalArgumentException("A negative number for maxNumberOfDiagnoses is not allowed");
    }

    @Override
    public String toString() {
        return "Preferences{" +
                "SESSION_KEEP_ALIVE_IN_MILLIS=" + SESSION_KEEP_ALIVE_IN_MILLIS +
                ", REASONER_TIMEOUT_IN_MILLIS=" + REASONER_TIMEOUT_IN_MILLIS +
                ", MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS=" + MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS +
                ", MAX_VISIBLE_CORRECT_AXIOMS=" + MAX_VISIBLE_CORRECT_AXIOMS +
                ", REASONER=" + reasonerId +
                ", MAX_NUMBER_OF_DIAGNOSES=" + maxNumberOfDiagnoses +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Preferences that = (Preferences) o;
        return MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS == that.MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS && MAX_VISIBLE_CORRECT_AXIOMS == that.MAX_VISIBLE_CORRECT_AXIOMS && maxNumberOfDiagnoses == that.maxNumberOfDiagnoses && SESSION_KEEP_ALIVE_IN_MILLIS.equals(that.SESSION_KEEP_ALIVE_IN_MILLIS) && REASONER_TIMEOUT_IN_MILLIS.equals(that.REASONER_TIMEOUT_IN_MILLIS) && reasonerId.equals(that.reasonerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(SESSION_KEEP_ALIVE_IN_MILLIS, REASONER_TIMEOUT_IN_MILLIS, MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS, MAX_VISIBLE_CORRECT_AXIOMS, reasonerId, maxNumberOfDiagnoses);
    }
}