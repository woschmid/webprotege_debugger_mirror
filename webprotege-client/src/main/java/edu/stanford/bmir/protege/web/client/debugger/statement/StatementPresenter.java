package edu.stanford.bmir.protege.web.client.debugger.statement;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.RootPanel;
import edu.stanford.bmir.protege.web.shared.debugger.Diagnosis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: Matthew Horridge<br> Stanford University<br> Bio-Medical Informatics Research Group<br> Date: 12/09/2013
 */
public class StatementPresenter {

    private static final int SEARCH_DELAY = 700;

    private static final int PAGE_SIZE = 200;

    public AcceptsOneWidget container;


    private StatementViewImpl view;

    public StatementPresenter() {
        this.view = new StatementViewImpl(this);
    }

    public void addCheckBoxClickhandler(CheckCheckBoxHandler checkCheckBox){
        view.setCheckCheckBox(checkCheckBox);
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

    public void addRepairsStatement(List<Diagnosis> msg){
        view.addRepairsStatement(msg);
    }

    public void addTestCasesStatement(Set<String> msg){
        view.addTestcasesStatement(msg);
    }


    public void clearAxoim(){
        view.table.clear();
        view.table.removeAllRows();
    }

    public Map<String, Boolean> getTableInfo() {
        Map<String, Boolean> answers = new HashMap<>();
        for(int i  = 0; i < view.table.getRowCount(); i++ ){
            String axiom = view.table.getText(i,0);
            CheckBox flagTrue = (CheckBox) view.table.getWidget(i,1);
            CheckBox flagFalse = (CheckBox) view.table.getWidget(i,2);
            if (flagTrue.getValue() == true){
                answers.put(axiom,true);
            }
            if (flagFalse.getValue() == true){
                answers.put(axiom,false);
            }
        }
        return answers;

    }
}
