package edu.stanford.bmir.protege.web.client.debugger.repairs;

import edu.stanford.bmir.protege.web.client.frame.ManchesterSyntaxFrameEditorPresenter;
import edu.stanford.bmir.protege.web.client.lang.DisplayNameRenderer;
import edu.stanford.bmir.protege.web.client.portlet.AbstractWebProtegePortletPresenter;
import edu.stanford.bmir.protege.web.client.portlet.PortletUi;
import edu.stanford.bmir.protege.web.client.selection.SelectionModel;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.webprotege.shared.annotations.Portlet;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Portlet(id = "portlet.Repairs", title = "Repairs")
public class RepairsPortletPresenter extends AbstractWebProtegePortletPresenter {

    @Nonnull
    public RepairsPresenter repairsPresenter;


    @Inject
    public RepairsPortletPresenter(@Nonnull SelectionModel selectionModel, @Nonnull ProjectId projectId, @Nonnull DisplayNameRenderer displayNameRenderer, @Nonnull RepairsPresenter repairsPresenter) {
        super(selectionModel, projectId, displayNameRenderer);
        this.repairsPresenter = repairsPresenter;
    }

    @Override
    public void startPortlet(PortletUi portletUi, WebProtegeEventBus eventBus) {
        repairsPresenter.start(portletUi,eventBus);
    }
}
