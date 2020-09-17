package edu.stanford.bmir.protege.web.client.debugger.statement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
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
        DiffClientBundle.INSTANCE.style().ensureInjected();
        this.statementPresenter = statementPresenter;
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void addQueriesStatement(Set<SafeHtml> axiomStatement){
        List<CheckBox> listcheckbox= new ArrayList<>();
        for (SafeHtml axiom :
                axiomStatement) {
            int row = table.getRowCount();
            Label statement = new HTML(axiom);
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
            SafeHtmlBuilder diagnosisString = new SafeHtmlBuilder();
            for (SafeHtml axiom:
                 diagnosis.getAxioms()) {
//                axioms = changeAxoimsStyle(axiom);
                diagnosisString.append(axiom);
                diagnosisString.append(new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml("<br/>"));


            }
            int row = table.getRowCount();
            Label repair = new Label("Repair #"+(numOfRepairs++));
            repair.getElement().getStyle().setColor("blue");
            Label statement = new HTML( diagnosisString.toSafeHtml() );
            table.setWidget(row,0,repair);
            table.setWidget(row+1,0,statement);
        }
    }

    public void addTestcasesStatement(Set<SafeHtml> axiomStatement){
        for (SafeHtml axiom :
                axiomStatement) {
            int row = table.getRowCount();
            Label statement =new HTML(axiom);
            table.setWidget(row, 0, statement);
        }
    }

    public void setCheckCheckBox(CheckCheckBoxHandler checkCheckBox) {
        this.checkCheckBox = checkCheckBox;
    }
}