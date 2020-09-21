package edu.stanford.bmir.protege.web.client.debugger;

import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.filter.FilterView;
import edu.stanford.bmir.protege.web.client.lang.DisplayNameRenderer;
import edu.stanford.bmir.protege.web.client.lang.DisplayNameSettingsManager;
import edu.stanford.bmir.protege.web.client.portlet.AbstractWebProtegePortletPresenter;
import edu.stanford.bmir.protege.web.client.portlet.PortletUi;
import edu.stanford.bmir.protege.web.client.search.SearchModal;
import edu.stanford.bmir.protege.web.client.selection.SelectionModel;
import edu.stanford.bmir.protege.web.client.tag.TagVisibilityPresenter;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.webprotege.shared.annotations.Portlet;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

@Portlet(id = "portlet.Debugger", title = "Debugger")
public class DebuggerPortletPresenter extends AbstractWebProtegePortletPresenter {

    private final DebuggerPresenter presenter;

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
    public DebuggerPortletPresenter(@Nonnull DebuggerPresenter presenter,
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
//        portletUi.setFilterView(filterView);
//        tagVisibilityPresenter.start(filterView, portletUi);
        presenter.installActions(portletUi);
        presenter.start(portletUi, eventBus);
        presenter.setHasBusy(portletUi);

//        handleAfterSetEntity(getSelectedEntity());
    }

}
