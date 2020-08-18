package edu.stanford.bmir.protege.web.client.debugger.queries;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.shared.debugger.*;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueriesPresenter {

    AcceptsOneWidget container;

    StatementPresenter statementPresenter;

    private QueriesView view;

    private final DispatchServiceManager dsm;

    @Nonnull
    private final ProjectId projectId;


    @Inject
    public QueriesPresenter(@Nonnull ProjectId projectId,
                            DispatchServiceManager dispatchServiceManager,
                            StatementPresenter statementPresenter, @Nonnull QueriesView queriesView) {
        this.statementPresenter = statementPresenter;
        this.view = queriesView;
        this.projectId = projectId;
        this.dsm = dispatchServiceManager;
        this.view = checkNotNull(queriesView);
    }


    public void start(AcceptsOneWidget container) {
        GWT.log("[QueriesPresenter]Start queries presenter");
        this.container = container;
        this.view.setStartDebuggingHandler(this::startDebugging);
        this.view.setStopDebuggingHandler(this::stopDebugging);
        this.view.setSubmitDebuggingHandler(this::submitDebugging);
        container.setWidget(view.asWidget());
        statementPresenter.start(view.getCriteriaContainer(),0);
    }

    private void startDebugging() {
        GWT.log("[QueriesPresenter]Start Debugging Button pressed!!!!!");
        this.dsm.execute(new StartDebuggingAction(projectId), new Consumer<DebuggingResult>() {
            @Override
            public void accept(DebuggingResult debuggingResult) {
                QueriesPresenter.this.onSuccess(debuggingResult.getQuery().toString());
            }
        });
    }

    private void stopDebugging() {
        GWT.log("[QueriesPresenter]Stop Debugging Button pressed!!!!!");
        this.dsm.execute(new StopDebuggingAction(projectId), new Consumer<StopDebuggingResult>() {
            @Override
            public void accept(StopDebuggingResult stopDebuggingResult) {
                QueriesPresenter.this.onSuccess(stopDebuggingResult.getMsg());
            }
        });
    }

    private void submitDebugging() {
        GWT.log("[QueriesPresenter]Submit Debugging Button pressed!!!!!");
        this.dsm.execute(new SubmitDebuggingAction(projectId), new Consumer<SubmitDebuggingResult>() {
            @Override
            public void accept(SubmitDebuggingResult submitDebuggingResult) {
                QueriesPresenter.this.onSuccess(submitDebuggingResult.getMsg());
            }
        });
    }

    public void clear() {

    }

    private void onSuccess(String msg) {
        GWT.log("[QueriesPresenter]Got Debugging Button Result successfully with msg: \"" + msg + "\"");
    }


}
