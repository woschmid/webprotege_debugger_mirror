package edu.stanford.bmir.protege.web.server.debugger;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.server.app.WebProtegeProperties;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.change.*;
import edu.stanford.bmir.protege.web.server.owlapi.RenameMap;
import edu.stanford.bmir.protege.web.server.project.ProjectDisposablesManager;
import edu.stanford.bmir.protege.web.server.project.ProjectManager;
import edu.stanford.bmir.protege.web.server.renderer.RenderingManager;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.HasDispose;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.GetEntityResult;
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

    private RevisionManager revisionManager;

    private IExquisiteProgressMonitor monitor;

    private final ScheduledExecutorService purgePreventionService;

    /**
     * Timestamp when the session has been actively used the last time.
     */
    private long lastActivityTimeInMillis;

    /**
     * Keep alive time span of a debugging session from it's last activity.
     * If this time span is exceeded the project (and it's debugging session) can be purged.
     */
    private static final Long SESSION_KEEPALIVE_IN_MILLIS = 10L * 60L * 1000L; // 10 minutes

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

        this.lastActivityTimeInMillis = System.currentTimeMillis();

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
                    ((System.currentTimeMillis() - lastActivityTimeInMillis) < SESSION_KEEPALIVE_IN_MILLIS)) {

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
     * @param userId
     * @return
     */
    public DebuggingSessionStateResult checkOntology(UserId userId) throws OWLOntologyCreationException {
        synchronized (this) {
            if (getUserId() != null)
                return DebuggingResultFactory.generateResult(this, Boolean.FALSE, "A debugging session is already running for this project by user " + getUserId());

            // guarantee that the session state is either in INIT or STOPPED state
            // let's not allow to check an already started session
            if (! (state==SessionState.INIT || state==SessionState.STOPPED) )
                throw new ActionExecutionException(new RuntimeException("Debugging session has been started already and cannot be started again"));

            loadOntology();

            this.userId = userId;

            this.lastActivityTimeInMillis = System.currentTimeMillis(); // new activity timestamp

            logger.info("{} Checking ontology and creating reasoner for {} in {} ...", this, userId, projectId);

            // creating a solver includes a possibly long-lasting consistency and coherency check
            this.state = SessionState.COMPUTING;
            this.consistencyCheckResult = ConsistencyChecker.checkConsistencyAndCoherency(ontology, diagnosisModel, ReasonerFactory.getReasonerFactory(), new LoggingReasonerProgressMonitor(this));
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
     */
    public DebuggingSessionStateResult start(@Nonnull UserId userId) throws OWLOntologyCreationException {
        synchronized (this) {
            if (!userId.equals(getUserId()))
                return DebuggingResultFactory.generateResult(this, Boolean.FALSE, "A debugging session is already running for this project by user " + getUserId());

            // guarantee that the session state is in CHECKED state
            if (state != SessionState.CHECKED || this.consistencyCheckResult == null)
                throw new ActionExecutionException(new RuntimeException("Debugging session can only be started when the ontology has been checked first."));

            // verify the result consistency check before we start a debugging session
            if (! (Boolean.FALSE.equals(this.consistencyCheckResult.isConsistent())
                    || Boolean.FALSE.equals(consistencyCheckResult.isCoherent())) )
                throw new ActionExecutionException(new RuntimeException("Debugging session cannot be started for a consistent and coherent ontology"));

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
     */
    public DebuggingSessionStateResult calculateQuery(@Nonnull UserId userId, @Nullable ImmutableMap<SafeHtml, Boolean> answers) {
        try {
            if (!userId.equals(getUserId()))
                return DebuggingResultFactory.generateResult(this, Boolean.FALSE, "A debugging session is already running for this project by user " + getUserId());

            // verify that the session state in STARTED state
            if (state != SessionState.STARTED)
                throw new RuntimeException("Debugging session is in unexpected state " + state + " and thus query calculation is not allowed.");

            this.lastActivityTimeInMillis = System.currentTimeMillis(); // new activity timestamp

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

    /**
     * Stops the running debugging session.
     *
     * @param userId The user who wants to stop a debugging session.
     * @return A result for the front end representing the current state of the backend.
     */
    public DebuggingSessionStateResult stop(@Nonnull UserId userId) {
        if (!userId.equals(getUserId()))
            return DebuggingResultFactory.generateResult(this, Boolean.FALSE,
                    "A debugging session is already running for this project by user " + getUserId());

        this.lastActivityTimeInMillis = System.currentTimeMillis(); // new activity timestamp
        stop();
        return DebuggingResultFactory.generateResult(this, Boolean.TRUE, null);
    }

    /**
     * Repairs the final diagnosis.
     *
     * @param userId The user who wants to repair the debugging session.
     * @param applyChanges Applying changes on
     * @return A result for the front end representing the current state of the backend.
     * @deprecated todo to be deleted
     */
    public DebuggingSessionStateResult repair(@Nonnull UserId userId, HasApplyChanges applyChanges) {
        if (!userId.equals(getUserId()))
            return DebuggingResultFactory.generateResult(this, Boolean.FALSE,
                    "A debugging session is already running for this project by user " + getUserId());

        // verify that the session state in STARTED state
        if (state != SessionState.STARTED)
            throw new RuntimeException("Debugging session is in unexpected state " + state + " and thus repair is not allowed.");

        // check the preconditions for a repair action
        if (!(query == null && diagnoses != null && ontologyID != null && diagnoses.size() == 1))
            throw new RuntimeException("A repair is not allowed!");

        this.lastActivityTimeInMillis = System.currentTimeMillis(); // new activity timestamp

        // get the final diagnosis and apply are remove axioms change operation for them
        final Diagnosis<OWLLogicalAxiom> diagnosis = diagnoses.iterator().next();

        final DebuggingSession debuggingSession = this;

        final ChangeListGenerator<Boolean> changeListGenerator = new ChangeListGenerator<>() {

            @Override
            public OntologyChangeList<Boolean> generateChanges(ChangeGenerationContext context) {
                final OntologyChangeList.Builder<Boolean> changeList = new OntologyChangeList.Builder<>();
                diagnosis.getFormulas().forEach(axiom -> changeList.removeAxiom(ontologyID, axiom));
                return changeList.build(true);
            }

            @Override
            public Boolean getRenamedResult(Boolean result, RenameMap renameMap) {
                return true;
            }

            @Nonnull
            @Override
            public String getMessage(ChangeApplicationResult<Boolean> result) {
                return "Repair action of " + debuggingSession;
            }
        };

        applyChanges.applyChanges(getUserId(), changeListGenerator);

        this.diagnoses = null;
        return DebuggingResultFactory.generateResult(this, Boolean.TRUE,
                "The ontology has been successfully repaired!");
    }

    /**
     * Deletes an axiom from the final diagnosis.
     *
     * @param userId The user who wants to delete a specific
     * @param axiomToDelete A safehtml representation of the axiom to remove.
     * @return
     * @deprecated to be deleted
     */
    public DebuggingSessionStateResult deleteRepairAxiom(@Nonnull UserId userId, @Nonnull HasApplyChanges applyChanges, @Nonnull SafeHtml axiomToDelete) {
        if (!userId.equals(getUserId()))
            return DebuggingResultFactory.generateResult(this, Boolean.FALSE,
                    "A debugging session is already running for this project by user " + getUserId());

        // verify that the session state in STARTED state
        if (state != SessionState.STARTED)
            throw new RuntimeException("Debugging session is in unexpected state " + state + " and thus repair is not allowed.");

        // check the preconditions for a repair action
        if (!(query == null && diagnoses != null && ontologyID != null && diagnoses.size() == 1))
            throw new RuntimeException("A repair is not allowed!");

        this.lastActivityTimeInMillis = System.currentTimeMillis(); // new activity timestamp

        // get the final diagnosis and apply are remove axioms change operation for them
        final Diagnosis<OWLLogicalAxiom> diagnosis = diagnoses.iterator().next();

        final DebuggingSession debuggingSession = this;

        final ChangeListGenerator<OWLLogicalAxiom> changeListGenerator = new ChangeListGenerator<>() {

            @Override
            public OntologyChangeList<OWLLogicalAxiom> generateChanges(ChangeGenerationContext context) {
                final OntologyChangeList.Builder<OWLLogicalAxiom> changeList = new OntologyChangeList.Builder<>();
                final OWLLogicalAxiom axiomToRemove = lookupAxiomInCollection(axiomToDelete, diagnosis.getFormulas());

                if (axiomToRemove != null) {
                    changeList.removeAxiom(ontologyID, axiomToRemove);
                    return changeList.build(axiomToRemove);
                } else
                    throw new RuntimeException("No matching axiom found in diagnosis to delete!");
            }

            @Override
            public OWLLogicalAxiom getRenamedResult(OWLLogicalAxiom axiom, RenameMap renameMap) {
                return axiom;
            }

            @Nonnull
            @Override
            public String getMessage(ChangeApplicationResult<OWLLogicalAxiom> result) {
                return "Repair action of " + debuggingSession;
            }
        };

        final ChangeApplicationResult<OWLLogicalAxiom> result = applyChanges.applyChanges(getUserId(), changeListGenerator);

        // remove the deleted axiom also from the diagnosis
        diagnosis.getFormulas().remove(result.getSubject());
        if (diagnosis.getFormulas().isEmpty())
            diagnoses = null;

        // and remove the deleted axiom also from diagnosis model
        getDiagnosisModel().getPossiblyFaultyFormulas().remove(result.getSubject());

        return DebuggingResultFactory.generateResult(this, Boolean.TRUE,
                    "The ontology has been successfully repaired!");
    }

    /**
     * Returns the owl entity belonging to an diagnosis axiomAsString.
     *
     * @param userId The user who wants to identify the owl entity behind an OWLLogicalAxiom.
     * @param axiomAsString The SafeHtml representation of an OWLLogicalAxiom which is expected to be in the diagnosis.
     * @return
     * @deprecated to be deleted
     */
    public GetEntityResult getEntity(UserId userId, SafeHtml axiomAsString) {
        if (!userId.equals(getUserId()))
            throw new RuntimeException("A debugging session is already running for this project by user " + getUserId());

        // verify that the session state in STARTED state
        if (state != SessionState.STARTED)
            throw new RuntimeException("Debugging session is in unexpected state " + state + " and thus repair is not allowed.");

        // check the preconditions for a repair action
        if (!(query == null && diagnoses != null && ontologyID != null && diagnoses.size() == 1))
            throw new RuntimeException("A repair is not allowed!");

        this.lastActivityTimeInMillis = System.currentTimeMillis(); // new activity timestamp

        // lookup the owl logical axiomAsString from the diagnosis axioms
        final OWLLogicalAxiom axiom = lookupAxiomInCollection(axiomAsString, diagnoses.iterator().next().getFormulas());

        if (axiom != null) {
            final OWLEntity owlEntity = axiom.accept(new OWLEntityExtractor());
            return new GetEntityResult(owlEntity);
        } else
            throw new RuntimeException("Axiom could not be found in diagnosis");

    }

    /**
     * Modifies an axiom from the final diagnosis.
     *
     * @param userId
     * @param applyChanges
     * @param originalAxiom
     * @param modifiedAxiom
     * @return
     * @deprecated todo: to be deleted
     */
    public DebuggingSessionStateResult modifyRepairAxiom(@Nonnull UserId userId, @Nonnull HasApplyChanges applyChanges, @Nonnull SafeHtml originalAxiom, @Nonnull SafeHtml modifiedAxiom) {
        // TODO implementation required
        return DebuggingResultFactory.generateResult(this, Boolean.TRUE, null);
    }

    /**
     * Moves an axiom between correct and possibly faulty axioms.
     *
     * @param userId The user who wants to move the axiom before starting a debugging session.
     * @param axiom The user who wants to stop a debugging session.
     * @return A result for the front end representing the current state of the backend.
     */
    public DebuggingSessionStateResult moveAxiomTo(UserId userId, SafeHtml axiom) {
        if (!userId.equals(getUserId()))
            return DebuggingResultFactory.generateResult(this, Boolean.FALSE,
                    "A debugging session is already running for this project by user " + getUserId());

        // verify that the session state in STARTED state
        if (state == SessionState.STARTED)
            throw new RuntimeException("Debugging session is in " + state + " and thus changing of the background is not allowed.");

        boolean hasMoved = moveBetween(axiom, getDiagnosisModel().getPossiblyFaultyFormulas(), getDiagnosisModel().getCorrectFormulas());
        if (!hasMoved)
            return DebuggingResultFactory.generateResult(this, Boolean.FALSE, "Move operation was not successful");
        return DebuggingResultFactory.generateResult(this, Boolean.TRUE, null);
    }



    /**
     * Moves the axiom between two lists.
     *
     * @param axiomAsString A SafeHtml representation of the axiom to move.
     * @param list1 List of axiom possibly containing the axiom to move from or move to.
     * @param list2 List of axiom possibly containing the axiom to move from or move to.
     * @return <code>true</code> if the axiom was found in one of the lists and moved to the other. <code>false</code> otherwise.
     */
    private boolean moveBetween(@Nonnull SafeHtml axiomAsString, @Nonnull List<OWLLogicalAxiom> list1, @Nonnull List<OWLLogicalAxiom> list2) {
        OWLLogicalAxiom a = lookupAxiomInCollection(axiomAsString, list1);
        if (a != null) {
            list1.remove(a);
            list2.add(a);
            return true;
        }
        a = lookupAxiomInCollection(axiomAsString, list2);
        if (a != null) {
            list2.remove(a);
            list1.add(a);
            return true;
        }
        return false;
    }

    /**
     * Removes an axiom from the testcases.
     *
     * @param userId The user who wants to stop a debugging session.
     * @param testCase The SafeHtml representation of the testcase to be removed.
     * @return A result for the front end representing the current state of the backend.
     */
    public DebuggingSessionStateResult removeTestCase(@Nonnull UserId userId, @Nonnull SafeHtml testCase) {
        if (!userId.equals(getUserId()))
            return DebuggingResultFactory.generateResult(this, Boolean.FALSE,
                    "A debugging session is already running for this project by user " + getUserId());

        // verify that the session state in STARTED state
        if (state != SessionState.STARTED)
            throw new RuntimeException("Debugging session is in unexpected state " + state + " and thus removing a test case is not allowed.");

        // search and remove in positive test cases
        final OWLLogicalAxiom positiveTestcase = lookupAxiomInCollection(testCase, getDiagnosisModel().getEntailedExamples());
        if (positiveTestcase != null) {
            getDiagnosisModel().getEntailedExamples().remove(positiveTestcase);
            return calculateQuery(userId, null);
        }

        // search and remove in negative test cases
        final OWLLogicalAxiom negativeTestcase = lookupAxiomInCollection(testCase, getDiagnosisModel().getNotEntailedExamples());
        if (negativeTestcase != null) {
            getDiagnosisModel().getNotEntailedExamples().remove(negativeTestcase);
            return calculateQuery(userId, null);
        }

        return DebuggingResultFactory.generateResult(this, Boolean.FALSE, "Testcase could not be found!");
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
        loadOntology();
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
     * Adds answers that were given to the previous query to the diagnosis model either as positive or as negative test cases.
     *
     * @param answers String representations of axioms from the previous query and the answers (either true or false) given to them.
     */
    private void addAnswersToDiagnosisModel(@Nonnull ImmutableMap<SafeHtml, Boolean> answers) {
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = engine.getSolver().getDiagnosisModel();
        for (Map.Entry<SafeHtml, Boolean> answer : answers.entrySet()) {
            // look up the axiom representing this string
            final OWLLogicalAxiom axiom = lookupAxiomInCollection(answer.getKey(), query.formulas);
            // if no axiom could be found, we have to throw an exception
            if (axiom != null) {
                if (answer.getValue())
                    diagnosisModel.getEntailedExamples().add(axiom);
                else
                    diagnosisModel.getNotEntailedExamples().add(axiom);
            } else
                throw new ActionExecutionException(new RuntimeException("Axiom could not be not found"));

        }
    }

    /**
     * Searches for an axiom in a collection that fits the SafeHtml representation and returns the actual axiom.
     *
     * @param axiomAsString The SafeHtml representation of the axiom to search for.
     * @param collection The collection searched within.
     * @return The axiom if a fitting or <code>null</code> if no axiom fitting the string representation was found.
     */
    @Nullable private OWLLogicalAxiom lookupAxiomInCollection(@Nonnull final SafeHtml axiomAsString, @Nonnull Collection<OWLLogicalAxiom> collection) {
        for (OWLLogicalAxiom axiom : collection)
            if (axiomAsString.equals(renderingManager.getHtmlBrowserText(axiom)))
                return axiom;
        return null;
    }

    private void loadOntology() {
        // loading ontology and generating diagnosis model
        final OWLOntologyManager ontologyManager = revisionManager.getOntologyManagerForRevision(revisionManager.getCurrentRevision());
        final List<OWLOntology> ontologies = new ArrayList<>(ontologyManager.getOntologies());
        if (ontologies.size() != 1)
            throw new ActionExecutionException(new RuntimeException("Expected only one ontology but there are " + ontologies.size()));

        this.ontology = ontologies.get(0);
        this.ontologyID = ontology.getOntologyID();
        logger.info("Found ontology {} from current revision {})", ontology, revisionManager.getCurrentRevision());

        this.diagnosisModel = ExquisiteOWLReasoner.generateDiagnosisModel(ontology, null);
        logger.info("Diagnosis model created {})", this.diagnosisModel);
    }
}
