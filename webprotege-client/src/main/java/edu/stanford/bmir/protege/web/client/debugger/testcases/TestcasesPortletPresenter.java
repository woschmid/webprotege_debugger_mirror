package edu.stanford.bmir.protege.web.client.debugger.testcases;

import edu.stanford.bmir.gwtcodemirror.client.AutoCompletionCallback;
import edu.stanford.bmir.gwtcodemirror.client.EditorPosition;
import edu.stanford.bmir.protege.web.client.debugger.ManchesterSyntaxEditor.DebuggerManchesterSyntaxFrameEditorPresenter;
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

@Portlet(id = "portlet.Testcases", title = "Testcases")

public class TestcasesPortletPresenter extends AbstractWebProtegePortletPresenter {

    @Nonnull
    public TestcasesPresenter testcasesPresenter;

    @Nonnull
    public ManchesterSyntaxFrameEditorPresenter debuggerManchesterSyntaxFrameEditorEPresenter;

    @Nonnull
    public ManchesterSyntaxFrameEditorPresenter debuggerManchesterSyntaxFrameEditorNPresenter;

    @Inject
    public TestcasesPortletPresenter(@Nonnull SelectionModel selectionModel, @Nonnull ProjectId projectId, @Nonnull DisplayNameRenderer displayNameRenderer, @Nonnull TestcasesPresenter presenter, @Nonnull ManchesterSyntaxFrameEditorPresenter debuggerManchesterSyntaxFrameEditorEPresenter ,ManchesterSyntaxFrameEditorPresenter debuggerManchesterSyntaxFrameEditorNPresenter) {
        super(selectionModel, projectId, displayNameRenderer);
        this.testcasesPresenter = presenter;
        this.debuggerManchesterSyntaxFrameEditorEPresenter = debuggerManchesterSyntaxFrameEditorEPresenter;
        this.debuggerManchesterSyntaxFrameEditorNPresenter = debuggerManchesterSyntaxFrameEditorNPresenter;
    }

    @Override
    public void startPortlet(PortletUi portletUi, WebProtegeEventBus eventBus) {
        debuggerManchesterSyntaxFrameEditorEPresenter.start(eventBus);
        debuggerManchesterSyntaxFrameEditorEPresenter.getView().setAutoCompletionHandler(this::nullFunction);
        debuggerManchesterSyntaxFrameEditorNPresenter.start(eventBus);
        debuggerManchesterSyntaxFrameEditorNPresenter.getView().setAutoCompletionHandler(this::nullFunction);
        testcasesPresenter.start(portletUi, eventBus);
        testcasesPresenter.setDebuggerManchesterSyntaxFrameEditorEPresenter(this.debuggerManchesterSyntaxFrameEditorEPresenter);
        testcasesPresenter.setDebuggerManchesterSyntaxFrameEditorNPresenter(this.debuggerManchesterSyntaxFrameEditorNPresenter);

    }

    private void nullFunction(String s, EditorPosition editorPosition, int i, AutoCompletionCallback autoCompletionCallback) {
    }

}
