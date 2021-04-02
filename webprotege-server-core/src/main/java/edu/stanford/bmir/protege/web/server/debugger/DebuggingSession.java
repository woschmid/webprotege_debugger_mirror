package edu.stanford.bmir.protege.web.server.debugger;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.server.app.WebProtegeProperties;
import edu.stanford.bmir.protege.web.server.change.ChangeApplicationResult;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.project.ProjectDisposablesManager;
import edu.stanford.bmir.protege.web.server.project.ProjectManager;
import edu.stanford.bmir.protege.web.server.renderer.RenderingManager;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.HasDispose;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.DefaultValues;
import edu.stanford.bmir.protege.web.shared.debugger.Preferences;
import edu.stanford.bmir.protege.web.shared.debugger.SessionState;
import edu.stanford.bmir.protege.web.shared.dispatch.ActionExecutionException;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import org.exquisite.core.DiagnosisException;
import org.exquisite.core.IExquisiteProgressMonitor;
import org.exquisite.core.costestimators.CardinalityCostEstimator;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ProjectSingleton
public class DebuggingSession implements HasDispose {

    private static final Logger logger = LoggerFactory.getLogger(DebuggingSession.class);

    /** Debugging session id */
    final private String id;

    /** The project the debugging session belongs to */
    final ProjectId projectId;

    /** The owner of this debugging session */
    private UserId userId;

    private OWLOntology ontology;

    private OWLOntologyID ontologyID;

    private DiagnosisModel<OWLLogicalAxiom> diagnosisModel;

    /** The current state of the debugging session */
    private SessionState state;

    private ConsistencyCheckResult consistencyCheckResult;

    /** The diagnosis engine used for this debugging session*/
    private IDiagnosisEngine<OWLLogicalAxiom> engine;

    /** Remember the query generated because of some diagnoses */
    private Query<OWLLogicalAxiom> query;

    /** Remember the diagnoses that were the base for the generated query */
    private Set<Diagnosis<OWLLogicalAxiom>> diagnoses;

    private RenderingManager renderingManager;

    private final RevisionManager revisionManager;

    private IExquisiteProgressMonitor monitor;

    private final ScheduledExecutorService purgePreventionService;

    /**
     * Timestamp when the session has been actively used the last time.
     */
    private long lastActivityTimeInMillis;

    /**
     * Searchfilter.
     */
    private final SearchFilter filter;

    /**
     * Current page for possibly faulty axioms.
     */
    private int currentPossiblyFaultyPage = 0;

    /**
     * Number of pages for possibly faulty axioms.
     */
    private int possiblyFaultyPages = 0;

    /**
     * Page index for correct axioms.
     */
    private int currentCorrectPage = 0;

    /**
     * Number of pages for correct axioms.
     */
    private int correctPages = 0;

    private Collection<OWLLogicalAxiom> presentedPossiblyFaultyAxioms = Collections.emptyList();

    private Collection<OWLLogicalAxiom> presentedCorrectAxioms = Collections.emptyList();

    /**
     * Number of possibly faulty axioms. Also regarding filters.
     */
    private int nrPossiblyFaultyAxioms = 0;

    /**
     * Number of correct axioms. Also regarding filters.
     */
    private int nrCorrectAxioms = 0;

    private Preferences preferences;

    @Inject
    public DebuggingSession(@Nonnull ProjectId projectId,
                            @Nonnull RevisionManager revisionManager,
                            @Nonnull RenderingManager renderingManager,
                            @Nonnull ProjectDisposablesManager disposablesManager,
                            @Nonnull ProjectManager projectManager,
                            @Nonnull WebProtegeProperties webProtegeProperties) {
        id = projectId.getId();

        this.projectId = projectId;
        this.renderingManager = renderingManager;
        this.revisionManager = revisionManager;

        this.state = SessionState.INIT;
        this.query = null;
        this.diagnoses = null;

        this.filter = new SearchFilter();

        //this.preferences = new Preferences(10L * 60L * 1000L, 10L * 60L * 1000L * 90 / 100, 10, 10, "HermiT", 0);
        this.preferences = new Preferences(
                DefaultValues.SESSION_KEEP_ALIVE_IN_MILLIS,
                DefaultValues.REASONER_TIMEOUT_IN_MILLIS,
                DefaultValues.MAX_VISIBLE_POSSIBLY_FAULTY_AXIOMS,
                DefaultValues.MAX_VISIBLE_CORRECT_AXIOMS,
                DefaultValues.reasonerId,
                DefaultValues.maxNumberOfDiagnoses
                );

        keepSessionAlive();

        // register this Debugging session to the DisposablesManager to free resources when project gets purged
        disposablesManager.register(this);

        // a service to prevent an active (defined by MAX_SESSION_KEEPALIVE_IN_MILLIS) debugging session and it's project
        // from being purged
        this.purgePreventionService = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setName(thread.getName().replace("thread", "debuggingsession-purgeprevention-service-thread"));
            return thread;
        });

        // this Thread's responsibility is to keep a running Debugging session alive if certain conditions are fulfilled
        final Runnable r = () -> {
            if (getUserId() != null // a debugging session has to be started ...
                    &&
                    (getState() == SessionState.STARTED || getState() == SessionState.COMPUTING) // .. it must be running
                    &&
                    // .. and must have been used within a certain time slot
                    ((System.currentTimeMillis() - lastActivityTimeInMillis) < preferences.getSessionKeepAliveInMillis())) {

                logger.info("Keeping project {} loaded for running {} ...", projectId, this);
                projectManager.ensureProjectIsLoaded(projectId, getUserId());
            }
        };

        // check interval is 90% of the project dormant time
        purgePreventionService.scheduleAtFixedRate(r,
                0,
                webProtegeProperties.getProjectDormantTime() * 90L / 100L,
                TimeUnit.MILLISECONDS);

        loadOntology();

        logger.info("{} created", this);
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper("DebuggingSession").addValue(id).toString();
    }

    /**
     * @return the owner of this debugging session.
     */
    @Nullable public UserId getUserId() {
        return userId;
    }

    /**
     * @return the current state the debugging session is in
     */
    @Nonnull public SessionState getState() {
        return state;
    }

    @Nullable public Query<OWLLogicalAxiom> getQuery() {
        return query;
    }

    @Nullable public Set<Diagnosis<OWLLogicalAxiom>> getDiagnoses() {
        return diagnoses;
    }

    @Nullable public DiagnosisModel<OWLLogicalAxiom> getDiagnosisModel() {
        return diagnosisModel;
    }

    public RenderingManager getRenderingManager() {
        return renderingManager;
    }

    public SearchFilter getSearchFilter() {
        return filter;
    }

    /** Return the currently set index for the shown possibly faulty axioms. */
    public int getCurrentPossiblyFaultyPage() {
        return currentPossiblyFaultyPage;
    }

    void setCurrentPossiblyFaultyPage(int currentPossiblyFaultyPage) {
        this.currentPossiblyFaultyPage = currentPossiblyFaultyPage;
    }

    public int getPossiblyFaultyPages() {
        return possiblyFaultyPages;
    }

    void setPossiblyFaultyPages(int possiblyFaultyPages) {
        this.possiblyFaultyPages = possiblyFaultyPages;
    }

    /** Return the currently set index for the shown correct axioms. */
    public int getCurrentCorrectPage() {
        return currentCorrectPage;
    }

    public void setCurrentCorrectPage(int currentCorrectPage) {
        this.currentCorrectPage = currentCorrectPage;
    }

    public int getCorrectPages() {
        return correctPages;
    }

    public void setCorrectPages(int correctPages) {
        this.correctPages = correctPages;
    }

    public void setPresentedPossiblyFaultyAxioms(Collection<OWLLogicalAxiom> presentedPossiblyFaultyAxioms) {
        this.presentedPossiblyFaultyAxioms = presentedPossiblyFaultyAxioms;
    }

    public Collection<OWLLogicalAxiom> getPresentedPossiblyFaultyAxioms() {
        return presentedPossiblyFaultyAxioms;
    }

    public void setPresentedCorrectAxioms(Collection<OWLLogicalAxiom> presentedCorrectAxioms) {
        this.presentedCorrectAxioms = presentedCorrectAxioms;
    }

    public Collection<OWLLogicalAxiom> getPresentedCorrectAxioms() {
        return presentedCorrectAxioms;
    }

    public int getNrPossiblyFaultyAxioms() {
        return nrPossiblyFaultyAxioms;
    }

    public void setNrPossiblyFaultyAxioms(int nrPossiblyFaultyAxioms) {
        this.nrPossiblyFaultyAxioms = nrPossiblyFaultyAxioms;
    }

    public int getNrCorrectAxioms() {
        return nrCorrectAxioms;
    }

    public void setNrCorrectAxioms(int nrCorrectAxioms) {
        this.nrCorrectAxioms = nrCorrectAxioms;
    }

    /**
     * Checks the ontology before starting a debugging session.
     * <ul>
     *     <li>Evaluate DPI</li>
     *     <li>Checks for consistency</li>
     *     <li>Checking for coherency</li>
     *     <li>Uses Module extraction</li>
     *     <li>NO Reduce to inconsistency</li>
     * </ul>
     *
     * @param userId The user who wants to check the ontology.
     * @return A result for the front end representing the current state of the backend.
     */
    public DebuggingSessionStateResult checkOntology(UserId userId) throws OWLOntologyCreationException, ConcurrentUserException, UnsatisfiedPreconditionException {
        synchronized (this) {
            checkUser(userId);

            // guarantee that the session state is either in INIT or STOPPED state
            // let's not allow to check an already started session
            verifyPreCondition(! (state==SessionState.INIT || state==SessionState.STOPPED) );

            logger.info("{} Checking ontology and creating reasoner for {} in {} ...", this, userId, projectId);

            // creating a solver includes a possibly long-lasting consistency and coherency check
            this.state = SessionState.COMPUTING;
            this.consistencyCheckResult = ConsistencyChecker.checkConsistencyAndCoherency(this, ontology, diagnosisModel, ReasonerFactory.getReasonerFactory(getPreferences().getReasonerId()), new LoggingReasonerProgressMonitor(this), getPreferences());
            this.diagnosisModel = consistencyCheckResult.getDiagnosisModel();

            // since we have done the check we can set the flag and return the appropriate result
            this.state = SessionState.CHECKED;

            if (Boolean.FALSE.equals(consistencyCheckResult.isConsistent())) {

                if (Boolean.FALSE.equals(consistencyCheckResult.isCoherent()))
                    // inconsistent and incoherent
                    return DebuggingResultFactory.generateResult(this, Boolean.TRUE, "The ontology is <strong>inconsistent</strong> and <strong>incoherent</strong>.<br/><br/>Please click on the <strong>Debug</strong> button to find the inconsistency and incoherency!");
                else
                    // inconsistent and undefined coherency
                    return DebuggingResultFactory.generateResult(this, Boolean.TRUE, "The ontology is <strong>inconsistent</strong>.<br/><br/>Please click on the <strong>Debug</strong> button to find the inconsistency!");

            } else if (Boolean.TRUE.equals(consistencyCheckResult.isConsistent())) {

                if (Boolean.FALSE.equals(consistencyCheckResult.isCoherent()))
                    // consistent but incoherent
                    return DebuggingResultFactory.generateResult(this, Boolean.TRUE, "The ontology is <strong>incoherent</strong>.<br/><br/>Please click on the <strong>Debug</strong> button to find the incoherency!");
                else if (Boolean.TRUE.equals(consistencyCheckResult.isCoherent())) {
                    // the ontology is consistent and coherent, we stop the session
                    stop();
                    return DebuggingResultFactory.generateResult(this, Boolean.TRUE, "The ontology is <strong>consistent</strong> and <strong>coherent</strong>");
                } else {
                    // consistent and undefined coherency, we stop the session
                    stop();
                    return DebuggingResultFactory.generateResult(this, Boolean.TRUE, "The ontology is <strong>consistent</strong>");
                }
            } else {
                // unexpected states
                throw new ActionExecutionException(new RuntimeException("Unexpected ontology check result: " + consistencyCheckResult.toString()));
            }
        }
    }

    /**
     * Starts a new debugging session.
     *
     * @param userId The user who wants to start a debugging session.
     * @return A result for the front end representing the current state of the backend.
     * @throws OWLOntologyCreationException occurred
     */
    public DebuggingSessionStateResult start(@Nonnull UserId userId) throws OWLOntologyCreationException, ConcurrentUserException, UnsatisfiedPreconditionException, AxiomNotFoundException {
        synchronized (this) {
            checkUser(userId);

            // guarantee that the session state is in CHECKED state
            // verify the result consistency check before we start a debugging session
            verifyPreCondition((state != SessionState.CHECKED || this.consistencyCheckResult == null)
                    ||
                    (! (Boolean.FALSE.equals(this.consistencyCheckResult.isConsistent())
                            || Boolean.FALSE.equals(consistencyCheckResult.isCoherent())) ));

            // reduce to inconsistency for incoherent ontologies
            if (Boolean.FALSE.equals(consistencyCheckResult.isCoherent()))
                ConsistencyChecker.reduceToInconsistency(diagnosisModel, monitor, consistencyCheckResult);

            // now lets create a new diagnosis engine and start the session
            this.engine = DiagnosisEngineFactory.createDiagnosisEngine(this.diagnosisModel, this);
            logger.info("{} Solver created: {}", this, this.engine.getSolver());
            logger.info("{} Diagnosis engine created: {}", this, this.engine);

            this.state = SessionState.STARTED;
            this.monitor = new LoggingQueryProgressMonitor(this);

            logger.info("{} started for {} in {}", this, userId, projectId);

            return calculateQuery(userId,null);
        }
    }

    /**
     * Submits the answers of the previous query if <code>not null</code> and calculates a new query.
     *
     * @param userId he user who wants to submit the answers and get the next query.
     * @param answers String representations of axioms from previous queries and their answers if given. Can be <code>null</code>.
     * @return A result for the front end representing the current state of the backend.
     * @throws AxiomNotFoundException if axiom for answer cannot be found
     */
    public DebuggingSessionStateResult calculateQuery(@Nonnull UserId userId, @Nullable ImmutableMap<SafeHtml, Boolean> answers) throws ConcurrentUserException, UnsatisfiedPreconditionException, AxiomNotFoundException {
        synchronized (this) {
            try {
                checkUser(userId);

                // verify that the session state in STARTED state
                verifyPreCondition(state != SessionState.STARTED);

                // reset the engine
                engine.resetEngine();

                // sets the cost estimator
                ((AbstractDiagnosisEngine) engine).setCostsEstimator(new CardinalityCostEstimator());

                // add possible answers
                // when we start a new session, then answers will be null,
                // otherwise this map should have at least one element
                if (answers != null && !answers.isEmpty())
                    addAnswersToDiagnosisModel(answers);

                state = SessionState.COMPUTING;
                // calculate diagnoses
                Set<Diagnosis<OWLLogicalAxiom>> newDiagnoses = new TreeSet<>(Comparator.reverseOrder());
                newDiagnoses.addAll(engine.calculateDiagnoses());
                logger.info("{} got {} diagnoses: {}", this, newDiagnoses.size(), newDiagnoses);

                if (newDiagnoses.size() >= 2) {
                    // there are more than one diagnoses, therefore we need to create a query for the user to narrow down the problem.

                    // configure and calculate the query
                    final HeuristicQueryComputation<OWLLogicalAxiom> queryComputation =
                            new HeuristicQueryComputation<>(
                                    new HeuristicConfiguration<>((AbstractDiagnosisEngine<OWLLogicalAxiom>) engine, monitor));

                    logger.info("{} calculating query from {} diagnoses ...", this, newDiagnoses.size());
                    queryComputation.initialize(newDiagnoses);

                    // get the calculated query
                    if (queryComputation.hasNext()) {
                        query = queryComputation.next();
                        diagnoses = newDiagnoses;
                        state = SessionState.STARTED;
                        logger.info("{} calculated query {}", this, query);
                        return DebuggingResultFactory.generateResult(this, Boolean.TRUE, null);
                    } else {
                        throw new RuntimeException("No query could be calculated.");
                    }
                } else if (newDiagnoses.size() == 1) {
                    // we found the diagnosis with the faulty axioms
                    // therefore there is no query generation necessary anymore

                    logger.info("{} found final diagnosis! {}", this, newDiagnoses);
                    query = null;
                    diagnoses = newDiagnoses;
                    state = SessionState.STARTED;
                    final int size = newDiagnoses.iterator().next().getFormulas().size();
                    return DebuggingResultFactory.generateResult(this, Boolean.TRUE,
                            "The debugger identified <strong>" + size +
                                    " faulty " + (size > 1 ? "axioms" : "axiom") + "</strong>.<br/><br/>" +
                                    "<strong>Modify</strong> or <strong>Delete</strong> " + ((size > 1) ? "them" : "it") + " to repair the ontology.");
                } else {
                    // diagnoses.size() == 0: the ontology is consistent and coherent and has therefore no diagnoses
                    logger.info("{} no diagnoses found -> ontology is consistent and coherent!", this);
                    query = null;
                    diagnoses = null;
                    state = SessionState.STARTED;
                    return DebuggingResultFactory.generateResult(this, Boolean.TRUE,
                            "The ontology is coherent and consistent!");
                }
            } catch (RuntimeException | DiagnosisException e) {
                throw new ActionExecutionException(e);
            }
        }
    }

    /**
     * Stops the running debugging session.
     *
     * @param userId The user who wants to stop a debugging session.
     * @return A result for the front end representing the current state of the backend.
     */
    public DebuggingSessionStateResult stop(@Nonnull UserId userId) throws ConcurrentUserException {
        synchronized (this) {
            checkUser(userId);
            stop();
            return DebuggingResultFactory.generateResult(this, Boolean.TRUE, null);
        }
    }

    /**
     * Repairs the final diagnosis.
     *
     * @param userId The user who wants to repair the debugging session.
     * @param axiomsToModify Some repair details (which axioms to delete, which to modify)
     * @param axiomsToDelete Some repair details (which axioms to delete, which to modify)
     * @param index The index of the diagnosis to be repaired.
     * @param applyChanges Applying changes on
     * @return A result for the front end representing the current state of the backend.
     */
    public DebuggingSessionStateResult repair(@Nonnull UserId userId, ImmutableMap<SafeHtml, String> axiomsToModify, ImmutableSet<SafeHtml> axiomsToDelete, Integer index, HasApplyChanges applyChanges) throws ConcurrentUserException, UnsatisfiedPreconditionException, OWLOntologyCreationException, RepairException {
        synchronized (this) {
            checkUser(userId);

            // verify that the session state in STARTED state
            // and check the preconditions for a repair action
            verifyPreCondition(state != SessionState.STARTED
                    ||
                    !(ontologyID != null && diagnoses != null && diagnoses.size() >= 1 && index >= 0 && index < diagnoses.size()));

            // get the diagnosis and apply change operation for them
            Diagnosis<OWLLogicalAxiom> diagnosis = new ArrayList<>(diagnoses).get(index);

            final RepairChangeListGenerator changeListGenerator = new RepairChangeListGenerator(this, diagnosis, axiomsToModify, axiomsToDelete);

            final ChangeApplicationResult<RepairChangeResult> result = applyChanges.applyChanges(getUserId(), changeListGenerator);

            if (result.getSubject().isValid()) {
                stop();
                return checkOntology(userId);
            } else
                throw result.getSubject().getException();
        }
    }

    /**
     * Moves all presented axioms from one to the other set.
     *
     * @param userId The user who wants to move axioms.
     * @param isMoveDown <code>true</code> means the currently shown possible faulty axioms (i.e. moveDown),
     *                   <code>false</code> means the correct axioms to be moved (i.e. moveUp).
     * @return A result for the front end representing the current state of the backend.
     * @throws ConcurrentUserException If the current debugging session is in use until stop by another user.
     * @throws UnsatisfiedPreconditionException If the precondition for an action is not fulfilled.
     */
    public DebuggingSessionStateResult moveAllAxiomsTo(UserId userId, Boolean isMoveDown) throws ConcurrentUserException, UnsatisfiedPreconditionException {
        synchronized (this) {
            checkUser(userId);

            // verify that the session is NOT in STARTED yet
            verifyPreCondition(state == SessionState.STARTED || state == SessionState.COMPUTING);

            if (isMoveDown) {
                // move all presented possibly faulty axioms to the correct axioms
                if (getDiagnosisModel() != null) {
                    getDiagnosisModel().getPossiblyFaultyFormulas().removeAll(getPresentedPossiblyFaultyAxioms());
                    getDiagnosisModel().getCorrectFormulas().addAll(getPresentedPossiblyFaultyAxioms());
                } else
                    throw new RuntimeException("Diagnosis model is unexpected to be null during moveAllAction!");
            } else {
                if (getDiagnosisModel() != null) {
                    // move all presented correct axioms to the possibly faulty axioms
                    getDiagnosisModel().getCorrectFormulas().removeAll(getPresentedCorrectAxioms());
                    getDiagnosisModel().getPossiblyFaultyFormulas().addAll(getPresentedCorrectAxioms());
                } else
                    throw new RuntimeException("Diagnosis model is unexpected to be null during moveAllAction!");
            }

            return DebuggingResultFactory.generateResult(this, Boolean.TRUE, null);
        }
    }

    /**
     * Moves an axiom between correct and possibly faulty axioms.
     *
     * @param userId The user who wants to move the axiom before starting a debugging session.
     * @param axiom The user who wants to stop a debugging session.
     * @return A result for the front end representing the current state of the backend.
     * @throws AxiomNotFoundException if axiom cannot be found.
     */
    public DebuggingSessionStateResult moveAxiomTo(UserId userId, SafeHtml axiom) throws ConcurrentUserException, UnsatisfiedPreconditionException, AxiomNotFoundException {
        synchronized (this) {
            checkUser(userId);

            // verify that the session is NOT in STARTED yet
            verifyPreCondition(state == SessionState.STARTED || state == SessionState.COMPUTING);

            if (getDiagnosisModel() != null)
                moveBetween(axiom, getDiagnosisModel().getPossiblyFaultyFormulas(), getDiagnosisModel().getCorrectFormulas());
            else
                throw new RuntimeException("Diagnosis model is unexpected to be null during moveToAction!");

            return DebuggingResultFactory.generateResult(this, Boolean.TRUE, null);
        }
    }

    /**
     * Moves the axiom between two lists.
     *
     * @param axiomAsString A SafeHtml representation of the axiom to move.
     * @param list1 List of axiom possibly containing the axiom to move from or move to.
     * @param list2 List of axiom possibly containing the axiom to move from or move to.
     * @throws AxiomNotFoundException if no axiom found.
     */
    private void moveBetween(@Nonnull SafeHtml axiomAsString, @Nonnull List<OWLLogicalAxiom> list1, @Nonnull List<OWLLogicalAxiom> list2) throws AxiomNotFoundException {
        try {
            OWLLogicalAxiom a = lookupAxiomInCollection(axiomAsString, list1);
            list1.remove(a);
            list2.add(a);
            return;
        } catch (AxiomNotFoundException e) { /* no comment */ }

        {
            OWLLogicalAxiom a = lookupAxiomInCollection(axiomAsString, list2);
            list2.remove(a);
            list1.add(a);
        }
    }

    /**
     * Removes an axiom from the testcases.
     *
     * @param userId The user who wants to stop a debugging session.
     * @param testCase The SafeHtml representation of the testcase to be removed.
     * @return A result for the frontend if the deletion was successful and representing the current state of the backend.
     */
    public DebuggingSessionStateResult removeTestCase(@Nonnull UserId userId, @Nonnull SafeHtml testCase) throws ConcurrentUserException, UnsatisfiedPreconditionException, AxiomNotFoundException {
        synchronized (this) {
            checkUser(userId);

            // a testcase can be removed anytime thus no session state check here

            // search and remove in positive test cases
            try {
                if (getDiagnosisModel()==null) throw new RuntimeException("Diagnosis model is unexpected to be null during removeTestCaseAction!");
                final OWLLogicalAxiom positiveTestcase = lookupAxiomInCollection(testCase, getDiagnosisModel().getEntailedExamples());
                getDiagnosisModel().getEntailedExamples().remove(positiveTestcase);

                if (state == SessionState.STARTED)
                    return calculateQuery(userId, null); // in a running session, lets recompute the query
                else
                    return DebuggingResultFactory.generateResult(this, Boolean.TRUE, null);
            } catch (AxiomNotFoundException e) { /* can happen */ }


            // search and remove in negative test cases
            final OWLLogicalAxiom negativeTestcase = lookupAxiomInCollection(testCase, getDiagnosisModel().getNotEntailedExamples());
            getDiagnosisModel().getNotEntailedExamples().remove(negativeTestcase);
            if (state == SessionState.STARTED)
                return calculateQuery(userId, null); // in a running session, lets recompute the query
            else
                return DebuggingResultFactory.generateResult(this, Boolean.TRUE, null);
        }
    }

    /**
     * Adds a testcase to the entailed or non-entailed testcases.
     *
     * @param userId The id of the user who wants to add the test case.
     * @param testCase A string representation of the axiom. This string will be parsed and evaluated. If the string is
     *                 not valid, an exception will be thrown.
     * @param isEntailed The type of test case. <code>true</code> means <i>entailed</i>, <code>false</code> means <i>non-entailed</i>.
     * @return A result for the frontend if addition was successful and representing the current state of the backend.
     */
    public DebuggingSessionStateResult addTestCase(@Nonnull UserId userId, @Nonnull String testCase, boolean isEntailed) throws ConcurrentUserException, UnsatisfiedPreconditionException, OWLParserException {
        synchronized (this) {
            checkUser(userId);

            // verify that the debugging session is not in a running state - addition of test cases is only possible before and after
            verifyPreCondition(state == SessionState.STARTED || state == SessionState.COMPUTING);

            final OWLLogicalAxiom axiom = OWLLogicalAxiomSyntaxParser.parse(ontology, testCase);

            if (isEntailed)
                this.diagnosisModel.getEntailedExamples().add(axiom);
            else
                this.diagnosisModel.getNotEntailedExamples().add(axiom);

            return DebuggingResultFactory.generateResult(this, Boolean.TRUE, null);
        }
    }

    /**
     * Checks the syntax and semantic correctness of an axiom represented in inline Manchester Syntax.
     * @param userId The id of the user who wants to check the syntax.
     * @param axiom A string representation of the axiom to be checked.
     * @return The current state of the backend for the frontend if the check was successful.
     * @throws ConcurrentUserException if the current debugging session is in use until stop by another user.
     * @throws UnsatisfiedPreconditionException If the precondition for this action is not fulfilled.
     * @throws OWLParserException Indicates that a parse error happened when trying to parse the string and thus the syntax is not valid.
     */
    public DebuggingSessionStateResult checkAxiomSyntax(@Nonnull UserId userId, @Nonnull String axiom) throws ConcurrentUserException, UnsatisfiedPreconditionException, OWLParserException {
        synchronized (this) {
            checkUser(userId);

            // verify that the session is in STARTED state
            verifyPreCondition(state != SessionState.STARTED);

            OWLLogicalAxiomSyntaxParser.parse(ontology, axiom);

            // no exception occurred, thus the string is a syntactically valid axiom expression
            return DebuggingResultFactory.generateResult(this, Boolean.TRUE, null);
        }
    }

    /**
     * Sets the search filter for the to be shown possibly faulty axioms in the frontend.
     * @param userId The id of the user who wants to change the search filter.
     * @param aBox <code>true</code> if a-box axioms to be shown.
     * @param tBox <code>true</code> if t-box axioms to be shown.
     * @param rBox <code>true</code> if r-box axioms to be shown.
     * @param searchString A search string.
     * @return The current state of the backend for the frontend.
     * @throws ConcurrentUserException if the current debugging session is in use until stop by another user.
     */
    public DebuggingSessionStateResult setSearchFilter(@Nonnull UserId userId, boolean aBox, boolean tBox, boolean rBox, @Nullable String searchString) throws ConcurrentUserException {
        synchronized (this) {
            checkUser(userId);

            // the search filter can be set anytime thus no session state check here

            // update filter
            filter.setABox(aBox);
            filter.setTBox(tBox);
            filter.setRBox(rBox);
            filter.setSearchString(searchString);

            // reset the currentPossiblyFaultyPage
            currentPossiblyFaultyPage = 0;
            possiblyFaultyPages = 0;

            // reset the currentCorrectPage
            currentCorrectPage = 0;
            correctPages = 0;

            return DebuggingResultFactory.generateResult(this, Boolean.TRUE, null);
        }
    }

    /**
     * Paginate through the shown possibly faulty axioms back and forward.
     *
     * @param userId The id of the user who wants to paginate through the shown possibly faulty axioms.
     * @param pageFlag The flag indicates which page shall be navigated.
     *                 <code>true</code> indicates the possibly faulty axioms.
     *                 <code>false</code> indicates the correct axioms.
     * @param page The page to be shown. Internal checks prevent out of bounds pagination.
     * @return The current state of the backend for the frontend.
     * @throws ConcurrentUserException if the current debugging session is in use until stop by another user.
     */
    public DebuggingSessionStateResult paginate(@Nonnull UserId userId, Boolean pageFlag, int page) throws ConcurrentUserException {
        synchronized (this) {
            checkUser(userId);

            if (pageFlag)
                currentPossiblyFaultyPage = Math.max(page, 1);
            else
                currentCorrectPage = Math.max(page, 1);

            return DebuggingResultFactory.generateResult(this, Boolean.TRUE, null);
        }
    }

    public DebuggingSessionStateResult setPreferences(@Nonnull UserId userId, @Nonnull Preferences preferences) throws ConcurrentUserException {
        synchronized (this) {
            checkUser(userId);

            if (!preferences.equals(this.preferences)) {
                this.preferences = preferences;
                stop();
            }

            return DebuggingResultFactory.generateResult(this, Boolean.TRUE, null);
        }
    }

    @Nonnull
    public Preferences getPreferences() {
        return preferences;
    }

    @Override
    public void dispose() {
        logger.info("Disposing {}", this);
        stop();
        renderingManager = null;
        engine = null;
        purgePreventionService.shutdown();
        ontologyID = null;
        ontology = null;
        diagnosisModel = null;
    }

    /**
     * Checks if the user is the initiator of the current running Debugging session.
     *
     * @param userId A user
     * @throws ConcurrentUserException if the current debugging session is in use until stop by another user.
     */
    private void checkUser(@Nonnull UserId userId) throws ConcurrentUserException {
        // no user yet defined for this debugging session? Now you are!
        if (getUserId() == null)
            this.userId = userId;

        if (!userId.equals(getUserId()))
            throw new ConcurrentUserException("The debugger is currently in use by user " + getUserId());

        // there is some activity from the user ongoing - therefore we keep the session alive to prevent a purge of the project
        keepSessionAlive();
    }

    /**
     * Keeping the current debugging session alive because of an activity from an authenticated user.
     */
    protected void keepSessionAlive() {
        this.lastActivityTimeInMillis = System.currentTimeMillis();
    }

    /**
     * Verifies if the precondition for an is fulfilled.
     *
     * @param conditionForFailure Is the precondition for an action fulfilled?
     * @throws UnsatisfiedPreconditionException If the precondition for an action is not fulfilled.
     */
    private void verifyPreCondition(boolean conditionForFailure) throws UnsatisfiedPreconditionException {
        if (conditionForFailure)
            throw new UnsatisfiedPreconditionException("This action is not allowed.<br>A precondition is not fulfilled!");
    }

    /**
     * Stops a debugging session, which can be restarted later.
     */
    protected void stop() {
        // dispose diagnosis engine, solver and everything else...
        // No check will be done on the session state beforehand.
        state = SessionState.STOPPED;
        userId = null;
        if (engine!=null)
            engine.dispose();
        monitor = null;
        query = null;
        diagnoses = null;
        consistencyCheckResult = null;
        filter.reset();
        currentPossiblyFaultyPage = 0;
        possiblyFaultyPages = 0;
        presentedPossiblyFaultyAxioms = Collections.emptyList();
        currentCorrectPage = 0;
        correctPages = 0;
        presentedCorrectAxioms = Collections.emptyList();
        nrPossiblyFaultyAxioms = 0;
        nrCorrectAxioms = 0;
        loadOntology();
    }

    /**
     * Adds answers that were given to the previous query to the diagnosis model either as positive or as negative test cases.
     *
     * @param answers String representations of axioms from the previous query and the answers (either true or false) given to them.
     */
    private void addAnswersToDiagnosisModel(@Nonnull ImmutableMap<SafeHtml, Boolean> answers) throws AxiomNotFoundException {
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = engine.getSolver().getDiagnosisModel();
        for (Map.Entry<SafeHtml, Boolean> answer : answers.entrySet()) {
            // look up the axiom representing this string
            final OWLLogicalAxiom axiom = lookupAxiomInCollection(answer.getKey(), query.formulas);
            if (answer.getValue())
                diagnosisModel.getEntailedExamples().add(axiom);
            else
                diagnosisModel.getNotEntailedExamples().add(axiom);

        }
    }

    /**
     * Searches for an axiom in a collection that fits the SafeHtml representation and returns the actual axiom.
     *
     * @param axiomAsString The SafeHtml representation of the axiom to search for.
     * @param collection The collection searched within.
     * @return The axiom if a fitting or <code>null</code> if no axiom fitting the string representation was found.
     * @throws AxiomNotFoundException If no appropriate axiom matching the SafeHtml representation was found in the collection.
     */
    @Nonnull protected OWLLogicalAxiom lookupAxiomInCollection(@Nonnull final SafeHtml axiomAsString, @Nonnull Collection<OWLLogicalAxiom> collection) throws AxiomNotFoundException{
        for (OWLLogicalAxiom axiom : collection)
            if (axiomAsString.equals(renderingManager.getHtmlBrowserText(axiom)))
                return axiom;
        logger.error("lookup failed for {} in {}", axiomAsString, collection);
        throw new AxiomNotFoundException(axiomAsString);
    }

    /**
     * Loads the ontology and generates a diagnosis model from this ontology.
     */
    private void loadOntology() {
        // loading ontology and generating diagnosis model
        final OWLOntologyManager ontologyManager = revisionManager.getOntologyManagerForRevision(revisionManager.getCurrentRevision());
        final List<OWLOntology> ontologies = new ArrayList<>(ontologyManager.getOntologies());
        if (ontologies.size() != 1)
            throw new ActionExecutionException(new RuntimeException("Expected only one ontology but there are " + ontologies.size()));

        this.ontology = ontologies.get(0);
        this.ontologyID = ontology.getOntologyID();
        logger.info("{} Found ontology {} from current revision {})", this, ontology, revisionManager.getCurrentRevision());

        this.diagnosisModel = ExquisiteOWLReasoner.generateDiagnosisModel(ontology, null);
        logger.info("{} Diagnosis model created {})", this, this.diagnosisModel);
    }

    protected OWLOntology getOntology() {
        return ontology;
    }

    protected OWLOntologyID getOntologyID() {
        return ontologyID;
    }

}
