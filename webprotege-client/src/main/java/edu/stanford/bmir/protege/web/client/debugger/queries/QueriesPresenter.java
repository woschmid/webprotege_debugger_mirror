package edu.stanford.bmir.protege.web.client.debugger.queries;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.CheckBox;
import edu.stanford.bmir.protege.web.client.debugger.statement.StatementPresenter;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueriesPresenter {

    AcceptsOneWidget container;

    private StatementPresenter statementPresenter;

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
        statementPresenter.addCheckBoxClickhandler(this::CheckCheckBox);
    }

    public StatementPresenter getStatementPresenter() {
        return statementPresenter;
    }



    public void start(AcceptsOneWidget container, StartDebuggingHandler starthandler, StopDebuggingHandler stophandler, SubmitDebuggingHandler submithandler) {
        GWT.log("[QueriesPresenter]Start queries presenter");
        this.container = container;
        this.view.setStartDebuggingHandler(starthandler);
        this.view.setStopDebuggingHandler(stophandler);
        this.view.setSubmitDebuggingHandler(submithandler);
        container.setWidget(view.asWidget());
        statementPresenter.start(view.getCriteriaContainer());
        setEnabledButton("stop");
    }

    public void clear() {

    }

    public void CheckCheckBox(ClickEvent event, CheckBox checkBoxOther, List<CheckBox> list) {
        boolean checked = ((CheckBox)event.getSource()).getValue();
        if (checked){
            checkBoxOther.setValue(false);
//            setEnabledButton("submit");
        }
        boolean flag = false;
        for ( CheckBox checkbox:
             list) {
            flag = checkbox.getValue() || flag;
        }
        if(flag){
            setEnabledButton("submit");
        }else{
            setEnabledButton("unsubmit");
        }
    }

//    public void setQueriesStatement(SafeHtml msg){
//        Set<String> items = new HashSet<String>(Arrays.asList(msg.split(", ")));
//        statementPresenter.addQueriesStatement(items);
//    }

    public void clearAxiomtable(){
        statementPresenter.clearAxoim();
    }

    public void setEnabledButton(String buttonTyp){
        if(buttonTyp.equals("stop")){
            view.disablebutton("stop");
            view.disablebutton("submit");
            view.enablebutton("start");
        }else if (buttonTyp.equals("start")){
            view.enablebutton("stop");
            view.disablebutton("submit");
            view.disablebutton("start");
        }else if (buttonTyp.equals("submit")){
            view.enablebutton("submit");
        }else if (buttonTyp.equals("unsubmit")){
            view.disablebutton("submit");
        }
    }

}
