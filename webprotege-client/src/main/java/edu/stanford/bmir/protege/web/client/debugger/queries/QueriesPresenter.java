package edu.stanford.bmir.protege.web.client.debugger.queries;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.shared.entity.EntityNode;
import edu.stanford.bmir.protege.web.shared.individuals.GetIndividualsAction;
import edu.stanford.bmir.protege.web.shared.pagination.Page;
import edu.stanford.bmir.protege.web.shared.pagination.PageRequest;

import javax.inject.Inject;
import java.util.Optional;
//import edu.stanford.bmir.protege.web.client.debuggerPlugin.statement.statementPresenter;

/**
 * Author: Matthew Horridge<br> Stanford University<br> Bio-Medical Informatics Research Group<br> Date: 12/09/2013
 */
public class QueriesPresenter {

    private static final int SEARCH_DELAY = 700;

    private static final int PAGE_SIZE = 200;

    AcceptsOneWidget container;

    StatementPresenter statementPresenter;


    private QueriesViewImpl view;

    @Inject
    public QueriesPresenter(StatementPresenter statementPresenter, QueriesViewImpl queriesView) {
        this.statementPresenter = statementPresenter;
        this.view = queriesView;

    }


    public void start(AcceptsOneWidget container) {
//        GWT.log("Application initialization complete.  Starting UI Initialization.");
//        GWT.log(view.toString());
        this.container = container;
        container.setWidget(view.asWidget());
//        statementPresenter = new StatementPresenter();
        statementPresenter.start(view.getCriteriaContainer(),0);
        view.setInstanceRetrievalTypeChangedHandler(this::handleRetrievalTypeChanged);
    }

    private void handleRetrievalTypeChanged() {
        updateList();
    }

    private void updateList() {
//        Optional<PageRequest> pageRequest = Optional.of(PageRequest.requestPageWithSize(view.getPageNumber(),
//                PAGE_SIZE));
//        GetIndividualsAction action = new GetIndividualsAction(projectId,
//                currentType,
//                view.getSearchString(),
//                view.getRetrievalMode(),
//                pageRequest);
//        dsm.execute(action, view, result -> {
//            Page<EntityNode> page = result.getPaginatedResult();
//            displayPageOfIndividuals(page);
//            selectionModel.getSelection().ifPresent(curSel -> {
//                if (!view.getSelectedIndividuals().equals(curSel)) {
//                    Optional<EntityNode> selectedIndividual = view.getSelectedIndividual();
//                    selectedIndividual.ifPresent(sel -> selectionModel.setSelection(sel.getEntity()));
//                    if (!selectedIndividual.isPresent()) {
//                        selectionModel.clearSelection();
//                    }
//                }
//            });
//        });
    }

    protected void addStatement(){
        statementPresenter.addQueriesStatement();
    }


    public void clear() {

    }


}
