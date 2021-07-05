package edu.stanford.bmir.protege.web.client.shaclTool.shaclResultTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.ProgressDisplay;
import edu.stanford.bmir.protege.web.client.shaclTool.ShaclResult;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Vector;

public class ShaclResultTablePresenter {
    @Nonnull
    private ProjectId projectId;

    private LoggedInUserProvider loggedInUserProvider;

    private DispatchServiceManager dsm;

    private ShaclResultTableViewImpl view;

    private DispatchErrorMessageDisplay errorDisplay;

    private ProgressDisplay progressDisplay;

    private ShaclResult shaclResult;

    WebProtegeEventBus eventBus;

    @Inject
    public ShaclResultTablePresenter(@Nonnull ProjectId projectId,
                                     DispatchServiceManager dispatchServiceManager,
                                     ShaclResultTableViewImpl view,
                                     LoggedInUserProvider loggedInUserProvider,
                                     DispatchErrorMessageDisplay errorDisplay,
                                     ProgressDisplay progressDisplay,
                                     ShaclResult shaclResult){
        this.projectId = projectId;
        this.loggedInUserProvider = loggedInUserProvider;
        this.dsm = dispatchServiceManager;
        this.view = view;
        this.errorDisplay = errorDisplay;
        this.progressDisplay = progressDisplay;
        this.shaclResult = shaclResult;

    }

    public void start(AcceptsOneWidget container, WebProtegeEventBus eventBus) {
        container.setWidget(view.asWidget());
        this.eventBus = eventBus;
        shaclResult.setShaclResultTablePresenter(this);
    }

    public void setEntity(OWLEntity owlEntity) {
//        GWT.log("getShaclValidationResult "+shaclResult.getShaclValidationResult().getValidationResult());
        if (shaclResult.getShaclValidationResult().getValidationResult() != null){
            GWT.log("ShaclResultTablePresenter "+owlEntity.toString());
            setData(shaclResult.getShaclValidationResult().getValidationResult());
        }else{
            GWT.log("ShaclResultTablePresenter Result = null");
        }
    }

    public void setData(List<Vector<String>> data){
        view.setTableTitle();
        view.addToTable(data);
    }
}
