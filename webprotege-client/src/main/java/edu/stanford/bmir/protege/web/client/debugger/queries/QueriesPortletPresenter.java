package edu.stanford.bmir.protege.web.client.debugger.queries;

import com.google.gwt.core.client.GWT;
import edu.stanford.bmir.protege.web.client.lang.DisplayNameRenderer;
import edu.stanford.bmir.protege.web.client.portlet.AbstractWebProtegePortletPresenter;
import edu.stanford.bmir.protege.web.client.portlet.PortletUi;
import edu.stanford.bmir.protege.web.client.selection.SelectionModel;
import edu.stanford.bmir.protege.web.shared.event.ProjectChangedEvent;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;
import edu.stanford.webprotege.shared.annotations.Portlet;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Portlet(id = "portlet.Queries", title = "Debugging Session")
public class QueriesPortletPresenter extends AbstractWebProtegePortletPresenter {

    private RevisionNumber lastRevisionNumber = RevisionNumber.getRevisionNumber(0);
    @Nonnull
    QueriesPresenter queriesPresenter;

    @Inject
    public QueriesPortletPresenter(@Nonnull SelectionModel selectionModel, @Nonnull ProjectId projectId, @Nonnull DisplayNameRenderer displayNameRenderer
                                , @Nonnull QueriesPresenter queriesPresenter) {
        super(selectionModel, projectId, displayNameRenderer);
        this.queriesPresenter = queriesPresenter;
    }

    @Override
    public void startPortlet(PortletUi portletUi, WebProtegeEventBus eventBus) {
        queriesPresenter.start(portletUi, eventBus);
        eventBus.addProjectEventHandler(getProjectId(),
                ProjectChangedEvent.TYPE, event -> handleProjectChanged(event));
    }

    private void handleProjectChanged(ProjectChangedEvent event) {
        GWT.log("[QueriesPortletPresenter] handleProjectChanged(" + event.toString() + ")");

        if(lastRevisionNumber.equals(event.getRevisionNumber())) {
            GWT.log("[QueriesPortletPresenter] handleProjectChanged returning ...");
            return;
        }

        GWT.log("[QueriesPortletPresenter] handleProjectChanged continuing ...");
        lastRevisionNumber = event.getRevisionNumber();
        queriesPresenter.stopDebuggingEvent();
    }
}
