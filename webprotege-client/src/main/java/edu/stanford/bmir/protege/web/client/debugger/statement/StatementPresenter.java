package edu.stanford.bmir.protege.web.client.debugger.statement;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.RootPanel;

import javax.inject.Inject;
import java.util.Set;

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


    public void start(AcceptsOneWidget container) {
        this.container = container;
        container.setWidget(view);
    }

    public void start(RootPanel widgets) {
        widgets.add(view);
    }

    public void addQueriesStatement(Set<String> msg){
        view.addQueriesStatement(msg);
    }

    public void addRepairsStatement(Set<String> msg){
        view.addRepairsStatement(msg);
    }

    public void addTestCasesStatement(Set<String> msg){
        view.addTestcasesStatement(msg);
    }


    public void clearAxoim(){
        view.table.clear();
        view.table.removeAllRows();
    }
}
