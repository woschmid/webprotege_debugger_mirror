package edu.stanford.bmir.protege.web.shared.debugger;

public class DefaultValues {

    public static final String REASONER_ID_HERMIT = "HermiT 1.4.3.456";
    /* public static final String REASONER_ID_PELLET = "Pellet 2.4.0-ignazio1977"; */
    public static final String REASONER_ID_JFACT = "JFact 4.0.4";
    public static final String REASONER_ID_JCEL = "JCEL 0.24.1";
    public static final String REASONER_ID_SNOROCKET = "Snorocket 4.0.1";
    public static final String REASONER_ID_ELK = "ELK 0.4.3";

    /**
     * Keep alive time span in milliseconds of a debugging session from it's last activity.
     * If this time span is exceeded the project (and it's debugging session) can be purged.
     */
    public static final Long SESSION_KEEP_ALIVE_IN_MILLIS = 10L * 60L * 1000L; // 10 minutes

    /**
     * Sets the reasonerId timeout to 90% of SESSION_KEEP_ALIVE_IN_MILLIS;
     */
    public static final Long REASONER_TIMEOUT_IN_MILLIS = SESSION_KEEP_ALIVE_IN_MILLIS * 90 / 100;

    /**
     * The number of maximal shown visible possibly faulty axioms
     */
    public static final int MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS = 10;

    /**
     * The number of maximal shown visible correct axioms
     */
    public static final int MAX_VISIBLE_CORRECT_AXIOMS = 10;

    /**
     * The id string of the currently used reasoner.
     */
    public static final String reasonerId = REASONER_ID_HERMIT;

    /**
     * The max number of Diagnoses. 0 means computation of all diagnoses.
     */
    public static final int maxNumberOfDiagnoses = 0;

}
