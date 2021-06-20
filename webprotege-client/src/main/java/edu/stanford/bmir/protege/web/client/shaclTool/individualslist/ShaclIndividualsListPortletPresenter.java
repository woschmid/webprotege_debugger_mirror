package edu.stanford.bmir.protege.web.client.shaclTool.individualslist;

import com.google.common.collect.ImmutableMap;
import com.google.gwt.core.client.GWT;
import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.filter.FilterView;
import edu.stanford.bmir.protege.web.client.lang.DisplayNameRenderer;
import edu.stanford.bmir.protege.web.client.lang.DisplayNameSettingsManager;
import edu.stanford.bmir.protege.web.client.portlet.AbstractWebProtegePortletPresenter;
import edu.stanford.bmir.protege.web.client.portlet.PortletAction;
import edu.stanford.bmir.protege.web.client.portlet.PortletUi;
import edu.stanford.bmir.protege.web.client.search.SearchModal;
import edu.stanford.bmir.protege.web.client.selection.SelectionModel;
import edu.stanford.bmir.protege.web.client.tag.TagVisibilityPresenter;
import edu.stanford.bmir.protege.web.shared.DataFactory;
import edu.stanford.bmir.protege.web.shared.event.ClassFrameChangedEvent;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.webprotege.shared.annotations.Portlet;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.semanticweb.owlapi.model.EntityType.*;

@Portlet(id = "portlets.ShaclIndividualsList", title = "Shacl Individuals by Class")
public class ShaclIndividualsListPortletPresenter extends AbstractWebProtegePortletPresenter {

    private final ShaclIndividualsListPresenter presenter;

    private final DisplayNameSettingsManager displayNameSettingsManager;

    @Nonnull
    private final FilterView filterView;

    @Nonnull
    private final TagVisibilityPresenter tagVisibilityPresenter;

    @Nonnull
    private final Messages messages;

    @Nonnull
    private final SearchModal searchModal;

    @Inject
    public ShaclIndividualsListPortletPresenter(@Nonnull ShaclIndividualsListPresenter presenter,
                                                @Nonnull SelectionModel selectionModel,
                                                @Nonnull ProjectId projectId,
                                                @Nonnull DisplayNameRenderer displayNameRenderer,
                                                @Nonnull DisplayNameSettingsManager displayNameSettingsManager,
                                                @Nonnull FilterView filterView,
                                                @Nonnull TagVisibilityPresenter tagVisibilityPresenter,
                                                @Nonnull Messages messages, @Nonnull SearchModal searchModal) {
        super(selectionModel, projectId, displayNameRenderer);
        this.presenter = checkNotNull(presenter);
        this.displayNameSettingsManager = checkNotNull(displayNameSettingsManager);
        this.filterView = checkNotNull(filterView);
        this.tagVisibilityPresenter = checkNotNull(tagVisibilityPresenter);
        this.searchModal = searchModal;
        this.messages = checkNotNull(messages);
    }

    @Override
    public void startPortlet(PortletUi portletUi, WebProtegeEventBus eventBus) {
        portletUi.setFilterView(filterView);
        tagVisibilityPresenter.start(filterView, portletUi);
        presenter.installActions(portletUi);
        portletUi.addAction(new PortletAction(messages.search(), "wp-btn-g--search", this::handleSearch));
        presenter.start(portletUi, eventBus);
        presenter.setDisplayLanguage(displayNameSettingsManager.getLocalDisplayNameSettings());
        handleAfterSetEntity(getSelectedEntity());
    }

    @Override
    protected void handleAfterSetEntity(Optional<OWLEntity> entity) {
        if ( !entity.isPresent() || !(getSelectedEntity().get().getEntityType() == CLASS)) {
//            setNothingSelectedVisible(true);
            setDisplayedEntity(Optional.empty());
        }else{
//            setNothingSelectedVisible(false);
            GWT.log("=========================");
            GWT.log(getSelectedEntity().get().getEntityType().toString());
            presenter.setClassEntity(entity.get());
        }
        Optional<OWLNamedIndividual> sel = entity.filter(OWLEntity::isOWLNamedIndividual)
                .map(e -> (OWLNamedIndividual) e);
        sel.ifPresent(presenter::setDisplayedIndividual);
    }

    private void handleSearch() {
        searchModal.setEntityTypes(NAMED_INDIVIDUAL);
        searchModal.showModal();
    }
}
