package edu.stanford.bmir.protege.web.client.debugger.repairInterface;



import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.frame.ManchesterSyntaxFrameEditorPresenter;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEvent;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class RepairInterfacePresenter{

    private RepairInterfaceViewImpl view;
    @Nonnull
    ManchesterSyntaxFrameEditorPresenter manchesterSyntaxFrameEditorPresenter;

    @Inject
    public RepairInterfacePresenter( @Nonnull ManchesterSyntaxFrameEditorPresenter manchesterSyntaxFrameEditorPresenter) {
        view = new RepairInterfaceViewImpl();
        this.manchesterSyntaxFrameEditorPresenter = manchesterSyntaxFrameEditorPresenter;
    }

    public void start(WebProtegeEventBus eventBus, DebuggingSessionStateResult debuggingSessionStateResult) {
        manchesterSyntaxFrameEditorPresenter.start(eventBus);
        setAxioms(debuggingSessionStateResult);
    }
    public void setAxioms(DebuggingSessionStateResult debuggingSessionStateResult){
        view.setAxioms(debuggingSessionStateResult.getDiagnoses().get(0).getAxioms());
    }

    public RepairInterfaceViewImpl getView() {
        return view;
    }
}
