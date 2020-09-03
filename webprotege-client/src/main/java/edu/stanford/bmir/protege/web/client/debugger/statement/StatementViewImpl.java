package edu.stanford.bmir.protege.web.client.debugger.statement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.shared.debugger.Diagnosis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class StatementViewImpl extends Composite{



    interface StatementViewImplUiBinder extends UiBinder<HTMLPanel, StatementViewImpl> {

    }

    private CheckCheckBoxHandler checkCheckBox = new CheckCheckBoxHandler() {
        @Override
        public void onClick(ClickEvent clickEvent, CheckBox checkBoxOther, List<CheckBox> list) {

        }
    };

    private static StatementViewImplUiBinder ourUiBinder = GWT.create(StatementViewImplUiBinder.class);

    @UiField
    protected FlexTable table;

    public List<RadioButton> radioGroups;

    protected StatementPresenter statementPresenter;

    public StatementViewImpl(StatementPresenter statementPresenter) {
        DiffClientBundle.INSTANCE.style().ensureInjected();
        this.statementPresenter = statementPresenter;
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void addQueriesStatement(Set<String> axiomStatement){
        List<CheckBox> listcheckbox= new ArrayList<>();
        for (String axiom :
                axiomStatement) {
            int row = table.getRowCount();
            Label statement = new Label(axiom);
            CheckBox checkBoxP = new CheckBox();
            setCheckboxStyle(checkBoxP, true);
            CheckBox checkBoxN = new CheckBox();
            setCheckboxStyle(checkBoxN, false);
            listcheckbox.add(checkBoxP);
            listcheckbox.add(checkBoxN);

            checkBoxN.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    checkCheckBox.onClick(clickEvent,checkBoxP, listcheckbox);
                }
            });

            checkBoxP.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    checkCheckBox.onClick(clickEvent,checkBoxN, listcheckbox);
                }
            });
            table.setWidget(row,0,statement);
            table.setWidget(row,1, checkBoxP);
            table.setWidget(row,2, checkBoxN);
        }
    }

    private void setCheckboxStyle( CheckBox checkBox , boolean b){
        DiffClientBundle.DiffCssResource style = DiffClientBundle.INSTANCE.style();
        StringBuilder sb = new StringBuilder();
        if (b){
            sb.append("<div class=\"").append( style.addBullet()).append(" \">").append("</div>");
        }else{
            sb.append("<div class=\"").append(style.removeBullet()).append(" \">").append("</div>");
        }
        checkBox.setHTML(sb.toString());
    }

    public void addRepairsStatement(List<Diagnosis> diagnoseStatement){
        int numOfRepairs = 1;

        for (Diagnosis diagnosis :
                diagnoseStatement) {
            String diagnosisString = "";
            for (String axiom:
                 diagnosis.getAxioms()) {
                String axioms = "";
                axioms = changeAxoimsStyle(axiom);
                diagnosisString += axioms +"</br>";

            }
            int row = table.getRowCount();
            Label repair = new Label("Repair #"+(numOfRepairs++));
            repair.getElement().getStyle().setColor("blue");
            Label statement = new HTML( diagnosisString );
            table.setWidget(row,0,repair);
            table.setWidget(row+1,0,statement);
        }
    }

    public void addTestcasesStatement(Set<String> axiomStatement){
        for (String axiom :
                axiomStatement) {
            int row = table.getRowCount();
            Label statement = new Label(axiom);
            Button rb0 = new Button("X");
            table.setWidget(row, 0, statement);
            table.setWidget(row, 1, rb0);
        }
    }

    private String changeAxoimsStyle(String axiom){
        DiffClientBundle.DiffCssResource style = DiffClientBundle.INSTANCE.style();;
        if(!axiom.equals("")) {
            return ("<span class=\"")+(style.lineElement())+("\">[")+(axiom)+("]</span>");
        }
        return "";
    }

    public void setCheckCheckBox(CheckCheckBoxHandler checkCheckBox) {
        this.checkCheckBox = checkCheckBox;
    }
}