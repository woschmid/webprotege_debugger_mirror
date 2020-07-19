package edu.stanford.bmir.protege.web.client.debugger.repairs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Jun 2018
 */
public class RepairsViewImpl extends Composite {

    @UiField
    SimplePanel criteriaContainer;

    RepairsPresenter repairsPresenter;

    interface repairsViewImplUiBinder extends UiBinder<HTMLPanel, RepairsViewImpl> {

    }
    private static repairsViewImplUiBinder ourUiBinder = GWT.create(repairsViewImplUiBinder.class);

//
//
//    @UiField
//    RadioButton indirectRadioButton;
//
//    @UiField
//    RadioButton directRadioButton;
//

//
//    private InstanceRetrievalTypeChangedHandler retrievalTypeChangedHandler = () -> {};


    public RepairsViewImpl(RepairsPresenter repairsPresenter) {
        this.repairsPresenter = repairsPresenter;
        initWidget(ourUiBinder.createAndBindUi(this));
    }

//    @UiHandler("nextButton")
//    protected void handleSearchStringChanged(ClickEvent event) {
//        repairsPresenter.clear();
//    }

    @Nonnull
    public AcceptsOneWidget getCriteriaContainer() {
        return criteriaContainer;
    }

}