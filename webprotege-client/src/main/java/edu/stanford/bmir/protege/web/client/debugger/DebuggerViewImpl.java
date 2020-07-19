package edu.stanford.bmir.protege.web.client.debugger;

import com.google.common.collect.ImmutableList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.list.ListBox;
import edu.stanford.bmir.protege.web.client.pagination.HasPagination;
import edu.stanford.bmir.protege.web.client.pagination.PaginatorPresenter;
import edu.stanford.bmir.protege.web.client.pagination.PaginatorView;
import edu.stanford.bmir.protege.web.client.progress.BusyView;
import edu.stanford.bmir.protege.web.client.search.SearchStringChangedHandler;
import edu.stanford.bmir.protege.web.shared.entity.EntityNode;
import edu.stanford.bmir.protege.web.shared.entity.OWLNamedIndividualData;
import edu.stanford.bmir.protege.web.shared.individuals.InstanceRetrievalMode;
import edu.stanford.bmir.protege.web.shared.lang.DisplayNameSettings;
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
public class DebuggerViewImpl extends Composite implements DebuggerView {

    private final PaginatorPresenter paginatorPresenter;

    private EntityNodeListCellRenderer renderer;

    interface IndividualsListViewImplUiBinder extends UiBinder<HTMLPanel, DebuggerViewImpl> {

    }

    private static IndividualsListViewImplUiBinder ourUiBinder = GWT.create(IndividualsListViewImplUiBinder.class);

    @UiField
    SimplePanel queriesContainer;

    @UiField
    SimplePanel repairsContainer;

    @UiField
    SimplePanel testcasesContainer;

    private SearchStringChangedHandler searchStringChangedHandler = () -> {};

    private InstanceRetrievalTypeChangedHandler retrievalTypeChangedHandler = () -> {};


    @Inject
    public DebuggerViewImpl(@Nonnull PaginatorPresenter paginatorPresenter,
                            @Nonnull EntityNodeListCellRenderer renderer) {
        this.paginatorPresenter = paginatorPresenter;
//        paginator = paginatorPresenter.getView();
        this.renderer = checkNotNull(renderer);
        initWidget(ourUiBinder.createAndBindUi(this));
//        individualsList.setRenderer(renderer);
//        individualsList.setKeyExtractor(node -> (OWLNamedIndividual) node.getEntity());
    }

    @Nonnull
    public AcceptsOneWidget getTypeFieldContainer() {
//        return typeFieldContainer;
        return null;
    }

    @Override
    public void setBusy(boolean busy) {
//        GWT.log("Set Busy: " + busy);
//        busyView.setVisible(busy);
    }
    @Nonnull
    public AcceptsOneWidget getQueriesContainer() {
        return queriesContainer;
    }

    @Nonnull
    public AcceptsOneWidget getRepairsContainer() {
        return repairsContainer;
    }

    @Nonnull
    public AcceptsOneWidget getTestcasesContainer() {
        return testcasesContainer;
    }

    @Override
    public void setDisplayLanguage(@Nonnull DisplayNameSettings language) {
//        renderer.setDisplayLanguage(language);
//        individualsList.setRenderer(renderer);
    }

    @Override
    public void updateNode(@Nonnull EntityNode entityNode) {
//        individualsList.updateElement(entityNode);
    }

    @Override
    public void setListData(List<EntityNode> individuals) {
//        individualsList.setListData(individuals);
    }

    @Override
    public void addListData(Collection<EntityNode> individuals) {
//        List<EntityNode> elements = individualsList.getElements();
//        elements.addAll(0, individuals);
//        individualsList.setListData(elements);
    }

    @Override
    public void removeListData(Collection<EntityNode> individuals) {
//        individualsList.setListData(ImmutableList.of());
    }

    @Override
    public Collection<EntityNode> getSelectedIndividuals() {
//        return individualsList.getSelection();
        return null;
    }

    @Override
    public Optional<EntityNode> getSelectedIndividual() {
//        return individualsList.getFirstSelectedElement();
        return null;
    }

    @Override
    public void setSelectedIndividual(OWLNamedIndividualData individual) {
//        individualsList.setSelection(individual.getEntity());
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<List<EntityNode>> handler) {
//        return individualsList.addSelectionHandler(handler);
        return null;
    }

    @Override
    public void setStatusMessage(String statusMessage) {
//        statusLabel.setText(statusMessage);
    }

    @Override
    public void setStatusMessageVisible(boolean visible) {
//        statusLabel.setVisible(visible);
    }

    @Override
    public String getSearchString() {
//        return searchBox.getText();
        return null;
    }

    @Override
    public void clearSearchString() {
//        searchBox.setText("");
    }

    @Nonnull
    @Override
    public InstanceRetrievalMode getRetrievalMode() {
//        if(indirectRadioButton.getValue()) {
//            return InstanceRetrievalMode.ALL_INSTANCES;
//        }
//        else {
//            return InstanceRetrievalMode.DIRECT_INSTANCES;
//        }
        return null;
    }

    @Override
    public void setRetrievalMode(@Nonnull InstanceRetrievalMode retrievalType) {
//        directRadioButton.setValue(retrievalType == InstanceRetrievalMode.DIRECT_INSTANCES);
//        indirectRadioButton.setValue(retrievalType == InstanceRetrievalMode.ALL_INSTANCES);
    }

    @Override
    public void setRetrievalModeEnabled(boolean enabled) {
//        indirectRadioButton.setEnabled(enabled);
//        directRadioButton.setEnabled(enabled);
    }


    @Override
    public void setSearchStringChangedHandler(@Nonnull SearchStringChangedHandler handler) {
        searchStringChangedHandler = checkNotNull(handler);
    }

    @Override
    public void setInstanceRetrievalTypeChangedHandler(@Nonnull InstanceRetrievalTypeChangedHandler handler) {
        this.retrievalTypeChangedHandler = checkNotNull(handler);
    }

    @Override
    public void setPageCount(int pageCount) {
        paginatorPresenter.setPageCount(pageCount);
    }

    @Override
    public void setPageNumber(int pageNumber) {
        paginatorPresenter.setPageNumber(pageNumber);
    }

    @Override
    public int getPageNumber() {
        return paginatorPresenter.getPageNumber();
    }

    @Override
    public void setPageNumberChangedHandler(HasPagination.PageNumberChangedHandler handler) {
        paginatorPresenter.setPageNumberChangedHandler(handler);
    }
}
