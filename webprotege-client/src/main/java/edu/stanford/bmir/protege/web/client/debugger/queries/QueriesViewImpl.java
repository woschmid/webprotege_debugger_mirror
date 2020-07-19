package edu.stanford.bmir.protege.web.client.debugger.queries;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 16/02/16
 */
public class QueriesViewImpl extends Composite{
    @UiField
    SimplePanel criteriaContainer;

    QueriesPresenter queriesPresenter;

    interface QeriesViewImplUiBinder extends UiBinder<HTMLPanel, QueriesViewImpl> {

    }

    private static QeriesViewImplUiBinder ourUiBinder = GWT.create(QeriesViewImplUiBinder.class);


    @UiField
    protected Button startButton;
    @UiField
    protected Button submitButton;


    public QueriesViewImpl(QueriesPresenter queriesPresenter) {
        this.queriesPresenter = queriesPresenter;
        initWidget(ourUiBinder.createAndBindUi(this));
    }



    @Nonnull
    public AcceptsOneWidget getCriteriaContainer() {
        return criteriaContainer;
    }

    @UiHandler("startButton")
    public void startButtonClick(ClickEvent event) {
        queriesPresenter.addStatement();
    }

    @UiHandler("submitButton")
    public void submitButtonClick(ClickEvent event) {

    }
}