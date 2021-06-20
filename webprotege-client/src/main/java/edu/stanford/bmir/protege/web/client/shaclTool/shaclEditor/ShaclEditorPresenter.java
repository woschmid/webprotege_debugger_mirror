package edu.stanford.bmir.protege.web.client.shaclTool.shaclEditor;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.portlet.PortletUi;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class ShaclEditorPresenter {

    @Nonnull
    private ProjectId projectId;

    private LoggedInUserProvider loggedInUserProvider;

    private DispatchServiceManager dsm;

    private ShaclViewImpl view;

    WebProtegeEventBus eventBus;

    @Inject
    public ShaclEditorPresenter(@Nonnull ProjectId projectId,
                                DispatchServiceManager dispatchServiceManager,
                                ShaclViewImpl view,
                                LoggedInUserProvider loggedInUserProvider){
        this.projectId = projectId;
        this.loggedInUserProvider = loggedInUserProvider;
        this.dsm = dispatchServiceManager;
        this.view = view;

    }

    public void start(AcceptsOneWidget container, WebProtegeEventBus eventBus) {
        container.setWidget(view.asWidget());
        this.eventBus = eventBus;
    }
}
