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
import edu.stanford.bmir.protege.web.shared.entity.EntityNode;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 16/02/16
 */
public class QueriesViewImpl extends Composite{
    @UiField
    SimplePanel criteriaContainer;

    EntityNodeListCellRenderer renderer;

    interface QeriesViewImplUiBinder extends UiBinder<HTMLPanel, QueriesViewImpl> {

    }

    private static QeriesViewImplUiBinder ourUiBinder = GWT.create(QeriesViewImplUiBinder.class);


    @UiField
    protected Button startButton;
    @UiField
    protected Button submitButton;
    @UiField
    protected ListBox<OWLNamedIndividual, EntityNode> axiomList;

    private InstanceRetrievalTypeChangedHandler retrievalTypeChangedHandler = () -> {};
    @Inject
    public QueriesViewImpl(@Nonnull EntityNodeListCellRenderer renderer) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.renderer = checkNotNull(renderer);
        axiomList.setRenderer(renderer);
        axiomList.setKeyExtractor(node -> (OWLNamedIndividual) node.getEntity());
    }

    public void setInstanceRetrievalTypeChangedHandler(@Nonnull InstanceRetrievalTypeChangedHandler handler) {
        this.retrievalTypeChangedHandler = checkNotNull(handler);
    }


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
}