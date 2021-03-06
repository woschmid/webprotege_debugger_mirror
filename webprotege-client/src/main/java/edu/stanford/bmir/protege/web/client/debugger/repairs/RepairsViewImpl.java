package edu.stanford.bmir.protege.web.client.debugger.repairs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import edu.stanford.bmir.protege.web.shared.entity.EntityNode;
import edu.stanford.bmir.protege.web.shared.pagination.Page;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Jun 2018
 */
public class RepairsViewImpl extends Composite implements RepairsView{

    @UiField
    SimplePanel criteriaContainer;

    interface repairsViewImplUiBinder extends UiBinder<HTMLPanel, RepairsViewImpl> {

    }
    private static repairsViewImplUiBinder ourUiBinder = GWT.create(repairsViewImplUiBinder.class);

    @Inject
    public RepairsViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setExecuteHandler(@Nonnull ExecuteQueryHandler handler) {

    }

    @Override
    public void setExecuteEnabled(boolean enabled) {

    }

    @Nonnull
    public AcceptsOneWidget getCriteriaContainer() {
        return criteriaContainer;
    }

    @UiHandler("helpButton")
    protected void helpButtonClick(ClickEvent event) { Window.open("https://git-ainf.aau.at/interactive-KB-debugging/debugger/-/wikis/faulty-axioms","_blank",""); }

    @Override
    public void clearResults() {

    }

    @Override
    public void setResult(@Nonnull Page<EntityNode> result) {

    }

}