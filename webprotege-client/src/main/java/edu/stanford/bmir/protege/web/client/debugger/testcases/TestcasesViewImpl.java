package edu.stanford.bmir.protege.web.client.debugger.testcases;

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
public class TestcasesViewImpl extends Composite {

    @UiField
    SimplePanel EntailedcriteriaContainer;

    @UiField
    SimplePanel NonEntailedcriteriaContainer;

    TestcasesPresenter testcasesPresenter;

    interface repairsViewImplUiBinder extends UiBinder<HTMLPanel, TestcasesViewImpl> {

    }

    private static repairsViewImplUiBinder ourUiBinder = GWT.create(repairsViewImplUiBinder.class);


    @Inject
    public TestcasesViewImpl( TestcasesPresenter testcasesPresenter) {
        this.testcasesPresenter = testcasesPresenter;
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