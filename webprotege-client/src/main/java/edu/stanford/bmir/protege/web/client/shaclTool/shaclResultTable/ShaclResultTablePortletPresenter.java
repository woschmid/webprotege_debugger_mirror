package edu.stanford.bmir.protege.web.client.shaclTool.shaclResultTable;

import com.google.gwt.core.client.GWT;
import edu.stanford.bmir.protege.web.client.editor.EditorPortletPresenter;
import edu.stanford.bmir.protege.web.client.lang.DisplayNameRenderer;
import edu.stanford.bmir.protege.web.client.portlet.AbstractWebProtegePortletPresenter;
import edu.stanford.bmir.protege.web.client.portlet.PortletUi;
import edu.stanford.bmir.protege.web.client.selection.SelectionModel;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.webprotege.shared.annotations.Portlet;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import java.util.Optional;

import static org.semanticweb.owlapi.model.EntityType.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 22 Mar 2017
 */
@Portlet(
        id = "portlet.ShaclResultTable",
        title = "Shacl Result Table",
        tooltip = "Provides an Shacl result table to show the details for selected entities"
)
public class ShaclResultTablePortletPresenter extends AbstractWebProtegePortletPresenter {

    private final ShaclResultTablePresenter resultTablePresenter;

    @Inject
    public ShaclResultTablePortletPresenter(@Nonnull SelectionModel selectionModel,
                                            @Nonnull ProjectId projectId,
                                            @Nonnull ShaclResultTablePresenter resultTablePresenter, DisplayNameRenderer displayNameRenderer) {
        super(selectionModel, projectId, displayNameRenderer);
        this.resultTablePresenter = resultTablePresenter;
    }

    @Override
    public void startPortlet(PortletUi portletUi, WebProtegeEventBus eventBus) {
        resultTablePresenter.start(portletUi, eventBus);
        handleAfterSetEntity(getSelectedEntity());
    }

    @Override
    protected void handleAfterSetEntity(Optional<OWLEntity> entity) {
        GWT.log("========================="+ entity);
        if ( entity.isPresent() || (getSelectedEntity().get().getEntityType() == NAMED_INDIVIDUAL)) {
            GWT.log("=========================");
            resultTablePresenter.setEntity(entity.get());
        }
    }
}
