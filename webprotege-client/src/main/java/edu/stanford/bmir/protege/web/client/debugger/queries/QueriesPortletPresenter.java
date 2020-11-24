package edu.stanford.bmir.protege.web.client.debugger.queries;

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

@Portlet(id = "portlet.Queries", title = "Debugging Session")
public class QueriesPortletPresenter extends AbstractWebProtegePortletPresenter {

    @Nonnull
    public ManchesterSyntaxFrameEditorPresenter manchesterSyntaxFrameEditorPresenter;

    @Nonnull
    QueriesPresenter queriesPresenter;

    @Inject
    public QueriesPortletPresenter(@Nonnull SelectionModel selectionModel,@Nonnull ManchesterSyntaxFrameEditorPresenter manchesterSyntaxFrameEditorPresenter, @Nonnull ProjectId projectId, @Nonnull DisplayNameRenderer displayNameRenderer
                                , @Nonnull QueriesPresenter queriesPresenter) {
        super(selectionModel, projectId, displayNameRenderer);
        this.queriesPresenter = queriesPresenter;
        this.manchesterSyntaxFrameEditorPresenter = manchesterSyntaxFrameEditorPresenter;
    }

    @Override
    public void startPortlet(PortletUi portletUi, WebProtegeEventBus eventBus) {
        manchesterSyntaxFrameEditorPresenter.start(eventBus);
        queriesPresenter.start(portletUi, eventBus);
        queriesPresenter.setManchesterSyntaxFrameEditorPresenter(this.manchesterSyntaxFrameEditorPresenter);
    }
}
