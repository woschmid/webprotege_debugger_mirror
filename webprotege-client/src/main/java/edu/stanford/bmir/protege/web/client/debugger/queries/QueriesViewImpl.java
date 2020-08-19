package edu.stanford.bmir.protege.web.client.debugger.queries;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
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

    @UiField
    protected Button startButton;

    @UiField
    protected Button submitButton;

    @UiField
    protected Button stopButton;

    interface QeriesViewImplUiBinder extends UiBinder<HTMLPanel, QueriesViewImpl> {

    }

    private static QeriesViewImplUiBinder ourUiBinder = GWT.create(QeriesViewImplUiBinder.class);

    private StartDebuggingHandler startDebuggingHandler = new StartDebuggingHandler() {
        @Override
        public void handleStartDebugging() {
        }
    };

    private StopDebuggingHandler stopDebuggingHandler = new StopDebuggingHandler() {
        @Override
        public void handleStopDebugging() {
        }
    };

    private SubmitDebuggingHandler submitDebuggingHandler = new SubmitDebuggingHandler() {
        @Override
        public void handleSubmitDebugging() {
        }
    };

    @Inject
    public QueriesViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @UiHandler("startButton")
    protected void handleStartDebugging(ClickEvent clickEvent) { startDebuggingHandler.handleStartDebugging();}

    @UiHandler("stopButton")
    protected void handleStopDebugging(ClickEvent clickEvent){ stopDebuggingHandler.handleStopDebugging();}

    @UiHandler("submitButton")
    protected void submitButtonClick(ClickEvent event) { submitDebuggingHandler.handleSubmitDebugging(); }

    @Override
    public void setStartDebuggingHandler(@Nonnull StartDebuggingHandler handler) { this.startDebuggingHandler = checkNotNull(handler); }

    @Override
    public void setStopDebuggingHandler(@Nonnull StopDebuggingHandler handler) { this.stopDebuggingHandler = checkNotNull(handler);}

    @Override
    public void setSubmitDebuggingHandler(@Nonnull SubmitDebuggingHandler handler){ this.submitDebuggingHandler = checkNotNull(handler);}

    @Nonnull
    public AcceptsOneWidget getCriteriaContainer() {
        return criteriaContainer;
    }

    @Override
    public void disablebutton(String s) {
        switch (s){
            case "start":
                startButton.setEnabled(false);
                break;
            case "stop":
                stopButton.setEnabled(false);
                break;
            case "submit":
                submitButton.setEnabled(false);
                break;
        }
    }

    public void enablebutton(String s) {
        switch (s){
            case "start":
                startButton.setEnabled(true);
                break;
            case "stop":
                stopButton.setEnabled(true);
                break;
            case "submit":
                submitButton.setEnabled(true);
                break;
        }
    }

}