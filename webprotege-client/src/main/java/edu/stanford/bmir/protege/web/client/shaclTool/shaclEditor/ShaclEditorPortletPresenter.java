package edu.stanford.bmir.protege.web.client.shaclTool.shaclEditor;

import edu.stanford.bmir.protege.web.client.lang.DisplayNameRenderer;
import edu.stanford.bmir.protege.web.client.portlet.AbstractWebProtegePortletPresenter;
import edu.stanford.bmir.protege.web.client.portlet.PortletUi;
import edu.stanford.bmir.protege.web.client.selection.SelectionModel;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.webprotege.shared.annotations.Portlet;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Portlet(id = "portlet.SchaclEditor", title = "Schacl Editor")
public class ShaclEditorPortletPresenter extends AbstractWebProtegePortletPresenter {

    @Nonnull
    public ShaclEditorPresenter editorPresenter;

    @Inject
    public ShaclEditorPortletPresenter(@Nonnull SelectionModel selectionModel,
                                       @Nonnull ProjectId projectId,
                                       @Nonnull DisplayNameRenderer displayNameRenderer,
                                       @Nonnull ShaclEditorPresenter presenter) {
        super(selectionModel, projectId, displayNameRenderer);
        this.editorPresenter = presenter;
    }

    @Override
    public void startPortlet(PortletUi portletUi, WebProtegeEventBus eventBus) {
        editorPresenter.start(portletUi,eventBus);
    }

}
