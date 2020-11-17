package edu.stanford.bmir.protege.web.client.debugger.testcases;

import edu.stanford.bmir.protege.web.client.lang.DisplayNameRenderer;
import edu.stanford.bmir.protege.web.client.portlet.AbstractWebProtegePortletPresenter;
import edu.stanford.bmir.protege.web.client.portlet.PortletUi;
import edu.stanford.bmir.protege.web.client.selection.SelectionModel;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.webprotege.shared.annotations.Portlet;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Portlet(id = "portlet.Testcases", title = "Testcases")

public class TestcasesPortletPresenter extends AbstractWebProtegePortletPresenter {

    @Nonnull
    public TestcasesPresenter testcasesPresenter;

    @Inject
    public TestcasesPortletPresenter(@Nonnull SelectionModel selectionModel, @Nonnull ProjectId projectId, @Nonnull DisplayNameRenderer displayNameRenderer, @Nonnull TestcasesPresenter presenter) {
        super(selectionModel, projectId, displayNameRenderer);
        this.testcasesPresenter = presenter;
    }

    @Override
    public void startPortlet(PortletUi portletUi, WebProtegeEventBus eventBus) {
        testcasesPresenter.start(portletUi, eventBus);
    }
}
