package edu.stanford.bmir.protege.web.client.debugger.queries;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.debugger.queries.EntityNodeListCellRenderer;
import edu.stanford.bmir.protege.web.client.individualslist.InstanceRetrievalTypeChangedHandler;
import edu.stanford.bmir.protege.web.client.list.ListBox;
import edu.stanford.bmir.protege.web.client.pagination.HasPagination;
import edu.stanford.bmir.protege.web.client.pagination.PaginatorPresenter;
import edu.stanford.bmir.protege.web.shared.entity.EntityNode;
import edu.stanford.bmir.protege.web.shared.individuals.InstanceRetrievalMode;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 16/02/16
 */
public class QueriesViewImpl extends Composite implements QueriesView {
    @UiField
    SimplePanel criteriaContainer;

    EntityNodeListCellRenderer renderer;

    interface QeriesViewImplUiBinder extends UiBinder<HTMLPanel, QueriesViewImpl> {

    }

    private static QeriesViewImplUiBinder ourUiBinder = GWT.create(QeriesViewImplUiBinder.class);
    PaginatorPresenter paginatorPresenter;

    private StartDebuggingHandler startDebuggingHandler = new StartDebuggingHandler() {
        @Override
        public void handleStartDebugging() {
        }
    };

    @UiField
    protected Button startButton;
    @UiField
    protected Button submitButton;
    @UiField
    protected ListBox<OWLNamedIndividual, EntityNode> axiomList;

    private InstanceRetrievalTypeChangedHandler retrievalTypeChangedHandler = () -> {};
    @Inject
    public QueriesViewImpl(@Nonnull PaginatorPresenter paginatorPresenter,
                           @Nonnull EntityNodeListCellRenderer renderer) {
        this.paginatorPresenter = paginatorPresenter;
        initWidget(ourUiBinder.createAndBindUi(this));
        this.renderer = checkNotNull(renderer);
        axiomList.setRenderer(renderer);
        axiomList.setKeyExtractor(node -> (OWLNamedIndividual) node.getEntity());
    }

    public void setInstanceRetrievalTypeChangedHandler(@Nonnull InstanceRetrievalTypeChangedHandler handler) {
        this.retrievalTypeChangedHandler = checkNotNull(handler);
    }

    @UiHandler("startButton")
    protected void handleStartDebugging(ClickEvent clickEvent) {startDebuggingHandler.handleStartDebugging();}

    @Override
    public void setStartDebuggingHandler(@Nonnull StartDebuggingHandler handler) { this.startDebuggingHandler = checkNotNull(handler); }

    @Nonnull
    public AcceptsOneWidget getCriteriaContainer() {
        return criteriaContainer;
    }

//    @UiHandler("startButton")
//    public void startButtonClick(ClickEvent event) {
//        queriesPresenter.addStatement();
//    }

    @UiHandler("submitButton")
    public void submitButtonClick(ClickEvent event) {

    }

    public void setPageCount(int pageCount) {
        paginatorPresenter.setPageCount(pageCount);
    }

    public void setPageNumber(int pageNumber) {
        paginatorPresenter.setPageNumber(pageNumber);
    }

    public int getPageNumber() {
        return paginatorPresenter.getPageNumber();
    }

    public void setPageNumberChangedHandler(HasPagination.PageNumberChangedHandler handler) {
        paginatorPresenter.setPageNumberChangedHandler(handler);
    }
    public InstanceRetrievalMode getRetrievalMode() {
//        if(indirectRadioButton.getValue()) {
//            return InstanceRetrievalMode.ALL_INSTANCES;
//        }
//        else {
//            return InstanceRetrievalMode.DIRECT_INSTANCES;
//        }
        return InstanceRetrievalMode.ALL_INSTANCES;
    }
    public Collection<EntityNode> getSelectedIndividuals() {
        return axiomList.getSelection();
    }

    public Optional<EntityNode> getSelectedIndividual() {
        return axiomList.getFirstSelectedElement();
    }

    public void setListData(List<EntityNode> individuals) {
        axiomList.setListData(individuals);
    }


    public String getSearchString() {
//        return searchBox.getText();
        return " ";
    }
}