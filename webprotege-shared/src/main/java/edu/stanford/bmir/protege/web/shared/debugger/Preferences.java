package edu.stanford.bmir.protege.web.shared.debugger;

import com.google.gwt.user.client.rpc.IsSerializable;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;

import java.io.Serializable;

public class Preferences implements Serializable, IsSerializable {

    /**
     * Keep alive time span in milliseconds of a debugging session from it's last activity.
     * If this time span is exceeded the project (and it's debugging session) can be purged.
     */
    private Long SESSION_KEEPALIVE_IN_MILLIS = 10L * 60L * 1000L; // 10 minutes

    /**
     * Sets the reasoner timeout to 90% of SESSION_KEEPALIVE_IN_MILLIS;
     */
    private Long REASONER_TIMEOUT_IN_MILLIS = SESSION_KEEPALIVE_IN_MILLIS * 90 / 100;

    /**
     * The number of maximal shown visible possibly faulty axioms
     */
    private int MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS = 10;

    /**
     * The number of maximal shown visible correct axioms
     */
    private int MAX_VISIBLE_CORRECT_AXIOMS = 10;

    @GwtSerializationConstructor
    private Preferences() {}

    public Preferences(Long SESSION_KEEPALIVE_IN_MILLIS, Long REASONER_TIMEOUT_IN_MILLIS, int MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS, int MAX_VISIBLE_CORRECT_AXIOMS) {
        this.SESSION_KEEPALIVE_IN_MILLIS = SESSION_KEEPALIVE_IN_MILLIS;
        this.REASONER_TIMEOUT_IN_MILLIS = REASONER_TIMEOUT_IN_MILLIS;
        this.MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS = MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS;
        this.MAX_VISIBLE_CORRECT_AXIOMS = MAX_VISIBLE_CORRECT_AXIOMS;
    }

    public Long getSessionKeepaliveInMillis() {
        return SESSION_KEEPALIVE_IN_MILLIS;
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

    /**
     * Possibility to change SESSION_KEEPALIVE_IN_MILLIS to a value in milliseconds between 1 minute to 6 hours.
     * @param sessionKeepaliveInMillis Time in milliseconds [1 minute ... 6 hours]
     */
    public void setSessionKeepaliveInMillis(Long sessionKeepaliveInMillis) {
        if (sessionKeepaliveInMillis >= 1000L * 60L && sessionKeepaliveInMillis <= 1000L * 60L * 60L + 6L) {
            SESSION_KEEPALIVE_IN_MILLIS = sessionKeepaliveInMillis;
            REASONER_TIMEOUT_IN_MILLIS = SESSION_KEEPALIVE_IN_MILLIS * 90 / 100;
        }
    }

    /**
     * Possibility to change MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS to a value between 1 and 50.
     * @param maxVisiblePossiblyFaultyAxioms Value between [1 .. 50]
     */
    public void setMaxVisiblePossiblyFaultyAxioms(int maxVisiblePossiblyFaultyAxioms) {
        if (maxVisiblePossiblyFaultyAxioms > 0 && maxVisiblePossiblyFaultyAxioms <= 50)
            MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS = maxVisiblePossiblyFaultyAxioms;
    }

    /**
     * Possibility to change MAX_VISIBLE_CORRECT_AXIOMS to a value between 1 and 50.
     * @param maxVisibleCorrectAxioms Value between [1 .. 50]
     */
    public void setMaxVisibleCorrectAxioms(int maxVisibleCorrectAxioms) {
        if (maxVisibleCorrectAxioms > 0 && maxVisibleCorrectAxioms <= 50)
            MAX_VISIBLE_CORRECT_AXIOMS = maxVisibleCorrectAxioms;
    }

    public void setReasonerTimeoutInMillis(Long reasonerTimeoutInMillis) {
        REASONER_TIMEOUT_IN_MILLIS = reasonerTimeoutInMillis;
    }

    @Override
    public String toString() {
        return "Preferences{" +
                "SESSION_KEEPALIVE_IN_MILLIS=" + SESSION_KEEPALIVE_IN_MILLIS +
                ", REASONER_TIMEOUT_IN_MILLIS=" + REASONER_TIMEOUT_IN_MILLIS +
                ", MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS=" + MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS +
                ", MAX_VISIBLE_CORRECT_AXIOMS=" + MAX_VISIBLE_CORRECT_AXIOMS +
                '}';
    }
}