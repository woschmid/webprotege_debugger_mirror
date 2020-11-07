package edu.stanford.bmir.protege.web.client.debugger.testcases;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Jun 2018
 */
public class TestcasesViewImpl extends Composite implements TestcasesView{

    @UiField
    SimplePanel EntailedcriteriaContainer;

    @UiField
    SimplePanel NonEntailedcriteriaContainer;

    @Override
    public AcceptsOneWidget getCriteriaContainer() {
        return EntailedcriteriaContainer;
    }

    interface repairsViewImplUiBinder extends UiBinder<HTMLPanel, TestcasesViewImpl> {

    }

    private static repairsViewImplUiBinder ourUiBinder = GWT.create(repairsViewImplUiBinder.class);


    @Inject
    public TestcasesViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @UiHandler("helpButton")
    protected void helpButtonClick(ClickEvent event) { Window.open("https://git-ainf.aau.at/interactive-KB-debugging/debugger/-/wikis/acquired-test-cases","_blank",""); }

    @Nonnull
    public AcceptsOneWidget getEntailedCriteriaContainer() {
        return EntailedcriteriaContainer;
    }

    @Nonnull
    public AcceptsOneWidget getNonEntailedcriteriaContainer() {
        return NonEntailedcriteriaContainer;
    }

}