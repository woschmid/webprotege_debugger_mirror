package edu.stanford.bmir.protege.web.client.debugger.statement;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.RootPanel;
import edu.stanford.bmir.protege.web.shared.debugger.Diagnosis;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: Matthew Horridge<br> Stanford University<br> Bio-Medical Informatics Research Group<br> Date: 12/09/2013
 */
public class StatementPresenter {

    public AcceptsOneWidget container;


    private StatementViewImpl view;

    @Inject
    public StatementPresenter() {
        this.view = new StatementViewImpl(this);
    }

    public void addCheckBoxClickhandler(CheckCheckBoxHandler checkCheckBox){
        view.setCheckCheckBox(checkCheckBox);
    }

    public void addFaultyAxiomRemoveHandler(FaultyAxiomRemoveHandler faultyAxiomRemoveHandler){
        view.setFaultyAxiomRemoveHandler(faultyAxiomRemoveHandler);
    }

    public void addBackgroundAxiomRemoveHandler(BackgroundAxiomRemoveHandler backgroundAxiomRemoveHandler){
        view.setBackgroundAxiomRemoveHandler(backgroundAxiomRemoveHandler);
    }


    public void start(AcceptsOneWidget container) {
        this.container = container;
        container.setWidget(view);
    }

    public void start(RootPanel widgets) {
        widgets.add(view);
    }

    public void addQueriesStatement(Set<SafeHtml> msg){
        view.addQueriesStatement(msg);
    }

    public void addRepairsStatement(List<Diagnosis> msg){
        view.addRepairsStatement(msg);
    }

    public void addTestCasesStatement(Set<SafeHtml> msg){ view.addTestcasesStatement(msg);}

    public void addPossibleFaultyAxioms(List<SafeHtml> backgroundAxioms,List<SafeHtml> possibleFaultyAxioms) {  view.updateBackground(backgroundAxioms, possibleFaultyAxioms);}


    public void clearAxoim(){
        view.table.clear();
        view.table.removeAllRows();
        view.queryAxioms.clear();
    }

    public Map<SafeHtml, Boolean> getTableInfo() {
        Map<SafeHtml, Boolean> answers = new HashMap<>();
        for(int i  = 0; i < view.table.getRowCount(); i++ ){
            //String axiom = view.table.getHTML(i,0);
            SafeHtml axiom = view.queryAxioms.get(i);
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
