package edu.stanford.bmir.protege.web.client.debugger.statement;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.RootPanel;

import javax.inject.Inject;

/**
 * Author: Matthew Horridge<br> Stanford University<br> Bio-Medical Informatics Research Group<br> Date: 12/09/2013
 */
public class StatementPresenter {

    private static final int SEARCH_DELAY = 700;

    private static final int PAGE_SIZE = 200;

    public AcceptsOneWidget container;


    private StatementViewImpl view;

    @Inject
    public StatementPresenter() {
        this.view = new StatementViewImpl(this);
    }


    public void start(AcceptsOneWidget container,int i) {
        this.container = container;
        if (i == 0){
            view.startQueriesStatement();
        } else if( i == 1){
            view.startRepairsStatement();
        }else{
            view.startTestcasesStatement();
        }
        container.setWidget(view);
    }

    public void start(RootPanel widgets) {
        view.startQueriesStatement();
        widgets.add(view);
    }

    public void addQueriesStatement(){
        view.addQueriesStatement();
    }
}
