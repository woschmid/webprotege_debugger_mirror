package edu.stanford.bmir.protege.web.client.debugger.statement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
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
        this.statementPresenter = statementPresenter;
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void addQueriesStatement(Set<String> axiomStatement){
        List<CheckBox> listcheckbox= new ArrayList<>();
        for (String axiom :
                axiomStatement) {
            int row = table.getRowCount();
            Label statement = new Label(axiom);
            CheckBox checkBoxP = new CheckBox("+");

            CheckBox checkBoxN = new CheckBox("-");

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

    public void addRepairsStatement(List<Diagnosis> diagnoseStatement){
        int numOfRepairs = 1;
        GWT.log("[addRepairsStatement] StyleName"+getStyleName() );
        for (Diagnosis diagnosis :
                diagnoseStatement) {
            String diagnosisString = "";
            for (String axiom:
                 diagnosis.getAxioms())
                diagnosisString += new SafeHtmlBuilder().appendEscaped(axiom).toSafeHtml().asString() + "<br/>";
            int row = table.getRowCount();
            Label repair = new Label("Repair #"+(numOfRepairs++));
            repair.getElement().getStyle().setColor("blue");
            Label statement = new HTML( diagnosisString );
//            statement.setStyleName("searchBox");
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

    public void setCheckCheckBox(CheckCheckBoxHandler checkCheckBox) {
        this.checkCheckBox = checkCheckBox;
    }
}