package edu.stanford.bmir.protege.web.client.debugger.background;

import edu.stanford.bmir.protege.web.client.lang.DisplayNameRenderer;
import edu.stanford.bmir.protege.web.client.portlet.AbstractWebProtegePortletPresenter;
import edu.stanford.bmir.protege.web.client.portlet.PortletUi;
import edu.stanford.bmir.protege.web.client.selection.SelectionModel;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.webprotege.shared.annotations.Portlet;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Portlet(id = "portlet.Background", title = "Ontology Axioms")

public class BackgroundPortletPresenter extends AbstractWebProtegePortletPresenter {

    @Nonnull
    public BackgroundPresenter background;

    @Inject
    public BackgroundPortletPresenter(@Nonnull SelectionModel selectionModel, @Nonnull ProjectId projectId, @Nonnull DisplayNameRenderer displayNameRenderer, @Nonnull BackgroundPresenter presenter) {
        super(selectionModel, projectId, displayNameRenderer);
        this.background = presenter;
    }

    @Override
    public void startPortlet(PortletUi portletUi, WebProtegeEventBus eventBus) {
        background.start(portletUi);
    }
}
