package edu.stanford.bmir.protege.web.client.debugger.background;

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
public class BackgroundViewImpl extends Composite implements BackgroundView {

    @UiField
    SimplePanel EntailedcriteriaContainer;

    @UiField
    SimplePanel NonEntailedcriteriaContainer;

    @Override
    public AcceptsOneWidget getCriteriaContainer() {
        return EntailedcriteriaContainer;
    }

    interface repairsViewImplUiBinder extends UiBinder<HTMLPanel, BackgroundViewImpl> {

    }

    private static repairsViewImplUiBinder ourUiBinder = GWT.create(repairsViewImplUiBinder.class);


    @Inject
    public BackgroundViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Nonnull
    public AcceptsOneWidget getEntailedCriteriaContainer() {
        return EntailedcriteriaContainer;
    }

    @Nonnull
    public AcceptsOneWidget getNonEntailedcriteriaContainer() {
        return NonEntailedcriteriaContainer;
    }

}