package edu.stanford.bmir.protege.web.client.debugger.background;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerPresenter;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerResultManager;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.Query;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 12 Jun 2018
 */

public class BackgroundPresnter extends DebuggerPresenter {

    @Nonnull
    private BackgroundView view;

    AcceptsOneWidget container;

    StatementPresenter statementPresenter1;
    StatementPresenter statementPresenter2;

    private final DebuggerResultManager debuggerResultManager;

    @Inject
    public BackgroundPresnter(@Nonnull ProjectId projectId, StatementPresenter statementPresenter1, StatementPresenter statementPresenter2,
                              DispatchServiceManager dispatchServiceManager,
                              MessageBox messageBox, StatementPresenter statementPresenter,
                              DispatchErrorMessageDisplay errorDisplay, ProgressDisplay progressDisplay, DebuggerResultManager debuggerResultManager, BackgroundView view, LoggedInUserProvider loggedInUserProvider) {
        super(statementPresenter, debuggerResultManager,view,loggedInUserProvider);
        this.statementPresenter1 = statementPresenter1;
        this.statementPresenter2 = statementPresenter2;
        this.view = view;

        this.debuggerResultManager = debuggerResultManager;
    }


    public void start(AcceptsOneWidget container) {
        container.setWidget(view.asWidget());
        debuggerResultManager.addToList(this);

        statementPresenter1 = new StatementPresenter();
        statementPresenter1.start(view.getEntailedCriteriaContainer());

        statementPresenter2 = new StatementPresenter();
        statementPresenter2.start(view.getNonEntailedcriteriaContainer());

        statementPresenter1.addFaultyAxiomRemoveHandler(this::handlerFaultyAxiomRemove);
        statementPresenter2.addBackgroundAxiomRemoveHandler(this::handlerBackgroundAxiomRemove);

    }

    public void setAxioms(DebuggingSessionStateResult debuggingSessionStateResult){
        setPossibleFaultyAxioms(debuggingSessionStateResult.getQuery());
    }

    List<SafeHtml> backgroundAxioms = new ArrayList<>();
    List<SafeHtml> possibleFaultyAxioms = new ArrayList<>();

    public void setPossibleFaultyAxioms(Query query){
        if (query != null) {
            Set<SafeHtml> items = query.getAxioms();
            for (SafeHtml axiom:items
                 ) {
                possibleFaultyAxioms.add(axiom);
            }
            setAxiomsToViews();
        }
    }

    private void setAxiomsToViews() {
        statementPresenter1.addPossibleFaultyAxioms( new ArrayList<>(),possibleFaultyAxioms);
        statementPresenter2.addPossibleFaultyAxioms( backgroundAxioms, new ArrayList<>());
    }

    public void handlerFaultyAxiomRemove(SafeHtml axiom){
        if (axiom != null) {
            backgroundAxioms.add(axiom);
            possibleFaultyAxioms.remove(axiom);
            setAxiomsToViews();
        }
    }
    public void handlerBackgroundAxiomRemove(SafeHtml axiom){
        if (axiom != null) {
            backgroundAxioms.remove((axiom));
            possibleFaultyAxioms.add(axiom);
            setAxiomsToViews();
        }
    }

    public void clearAxiomtable() {
        backgroundAxioms.clear();
        possibleFaultyAxioms.clear();
        statementPresenter1.clearAxoim();
        statementPresenter2.clearAxoim();
    }
}
