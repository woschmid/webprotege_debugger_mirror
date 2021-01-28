package edu.stanford.bmir.protege.web.server.debugger;

public class Preferences {

    /**
     * Keep alive time span in milliseconds of a debugging session from it's last activity.
     * If this time span is exceeded the project (and it's debugging session) can be purged.
     */
    static Long SESSION_KEEPALIVE_IN_MILLIS = 10L * 60L * 1000L; // 10 minutes

    /**
    * The number of maximal shown visible possibly faulty axioms
     */
    static int MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS = 15;


    protected static void setSessionKeepaliveInMillis(Long sessionKeepaliveInMillis) {
        SESSION_KEEPALIVE_IN_MILLIS = sessionKeepaliveInMillis;
    }

    protected static void setMaxVisiblePossiblyFaultyAxioms(int maxVisiblePossiblyFaultyAxioms) {
        MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS = maxVisiblePossiblyFaultyAxioms;
    }
}