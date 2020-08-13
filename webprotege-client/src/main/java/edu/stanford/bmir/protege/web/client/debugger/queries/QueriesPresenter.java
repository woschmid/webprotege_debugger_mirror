package edu.stanford.bmir.protege.web.client.debugger.queries;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.action.UIAction;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerViewImpl;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.entity.CreateEntityPresenter;
import edu.stanford.bmir.protege.web.client.entity.EntityNodeUpdater;
import edu.stanford.bmir.protege.web.client.hierarchy.HierarchyFieldPresenter;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.permissions.LoggedInUserProjectPermissionChecker;
import edu.stanford.bmir.protege.web.shared.individuals.GetIndividualsPageContainingIndividualAction;
import edu.stanford.bmir.protege.web.shared.individuals.GetIndividualsPageContainingIndividualResult;
import edu.stanford.bmir.protege.web.client.portlet.PortletAction;
import edu.stanford.bmir.protege.web.client.selection.SelectionModel;
import edu.stanford.bmir.protege.web.shared.entity.EntityNode;
import edu.stanford.bmir.protege.web.shared.individuals.GetIndividualsAction;
import edu.stanford.bmir.protege.web.shared.individuals.GetIndividualsResult;
import edu.stanford.bmir.protege.web.shared.pagination.Page;
import edu.stanford.bmir.protege.web.shared.pagination.PageRequest;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;
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

    private final DispatchServiceManager dsm;

    private final HierarchyFieldPresenter hierarchyFieldPresenter;

    private final Messages messages;

    @Nonnull
    private final ProjectId projectId;

    private final SelectionModel selectionModel;

    private final LoggedInUserProjectPermissionChecker permissionChecker;

    @Nonnull
    private final CreateEntityPresenter createEntityPresenter;

    private final Map<OWLEntity, EntityNode> elementsMap = new HashMap<>();

    private final EntityNodeUpdater entityNodeUpdater;

    private Optional<OWLClass> currentType = Optional.empty();

    private MessageBox messageBox;

    private final Timer searchStringDelayTimer = new Timer() {
        public void run() {
            updateList();
        }
    };

    @Inject
    public QueriesPresenter(@Nonnull ProjectId projectId,
                            final SelectionModel selectionModel,
                            DispatchServiceManager dispatchServiceManager,
                            LoggedInUserProjectPermissionChecker permissionChecker,
                            HierarchyFieldPresenter hierarchyFieldPresenter,
                            Messages messages,
                            @Nonnull CreateEntityPresenter createEntityPresenter, EntityNodeUpdater entityNodeUpdater, MessageBox messageBox,
                            StatementPresenter statementPresenter, QueriesViewImpl queriesView) {
        this.statementPresenter = statementPresenter;
        this.view = queriesView;

        this.projectId = projectId;
        this.selectionModel = selectionModel;
        this.permissionChecker = permissionChecker;
        this.dsm = dispatchServiceManager;
        this.hierarchyFieldPresenter = hierarchyFieldPresenter;
        this.messages = messages;
        this.createEntityPresenter = createEntityPresenter;
        this.entityNodeUpdater = entityNodeUpdater;
        this.messageBox = messageBox;
        this.view.setPageNumberChangedHandler(pageNumber -> updateList());

    }


    public void start(AcceptsOneWidget container) {
//        GWT.log("Application initialization complete.  Starting UI Initialization.");
        GWT.log("[QueriesPresenter]Start queries presenter");
        this.container = container;
        container.setWidget(view.asWidget());
//        statementPresenter = new StatementPresenter();
        statementPresenter.start(view.getCriteriaContainer(),0);
        view.setInstanceRetrievalTypeChangedHandler(this::handleRetrievalTypeChanged);
        updateList();
    }

    private void handleRetrievalTypeChanged() {
        updateList();
    }

    private void updateList() {

        Optional<PageRequest> pageRequest = Optional.of(PageRequest.requestPageWithSize(view.getPageNumber(),
                PAGE_SIZE));
        GWT.log("[QueriesPresenter]UpdateList");
        GetIndividualsAction action = new GetIndividualsAction(projectId,
                currentType,
                view.getSearchString(),
                view.getRetrievalMode(),
                pageRequest);
        GWT.log("[QueriesPresenter]Action is"+ action.toString());
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

    private void displayPageOfIndividuals(Page<EntityNode> page) {
        Collection<EntityNode> curSel = view.getSelectedIndividuals();
        List<EntityNode> individuals = page.getPageElements();
        elementsMap.clear();
        individuals.forEach(node -> elementsMap.put(node.getEntity(), node));
        view.setListData(individuals);
        view.setPageCount(page.getPageCount());
        view.setPageNumber(page.getPageNumber());

    }

    protected void addStatement(){
        statementPresenter.addQueriesStatement();
    }


    public void clear() {

    }


}