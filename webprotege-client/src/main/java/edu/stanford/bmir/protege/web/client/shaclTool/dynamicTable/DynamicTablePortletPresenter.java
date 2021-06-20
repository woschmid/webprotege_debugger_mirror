package edu.stanford.bmir.protege.web.client.shaclTool.dynamicTable;

import edu.stanford.bmir.protege.web.client.editor.EditorPortletPresenter;
import edu.stanford.bmir.protege.web.client.lang.DisplayNameRenderer;
import edu.stanford.bmir.protege.web.client.portlet.AbstractWebProtegePortletPresenter;
import edu.stanford.bmir.protege.web.client.portlet.PortletUi;
import edu.stanford.bmir.protege.web.client.selection.SelectionModel;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.webprotege.shared.annotations.Portlet;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static org.semanticweb.owlapi.model.EntityType.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 22 Mar 2017
 */
@Portlet(
        id = "portlet.DynamicTable",
        title = "DynamicTable",
        tooltip = "Provides an dynamic table to show the details for selected entities"
)
public class DynamicTablePortletPresenter extends AbstractWebProtegePortletPresenter {

    private final EditorPortletPresenter editorPresenter;

    @Inject
    public DynamicTablePortletPresenter(@Nonnull SelectionModel selectionModel,
                                        @Nonnull ProjectId projectId,
                                        @Nonnull EditorPortletPresenter editorPresenter, DisplayNameRenderer displayNameRenderer) {
        super(selectionModel, projectId, displayNameRenderer);
        this.editorPresenter = editorPresenter;
    }

    @Override
    public void startPortlet(PortletUi portletUi, WebProtegeEventBus eventBus) {
        editorPresenter.setDisplayedTypes(
                                          NAMED_INDIVIDUAL
                                          );
        editorPresenter.start(portletUi, eventBus);
    }
}
