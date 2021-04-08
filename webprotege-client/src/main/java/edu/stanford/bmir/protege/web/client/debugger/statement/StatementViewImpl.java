package edu.stanford.bmir.protege.web.client.debugger.statement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import edu.stanford.bmir.protege.web.client.debugger.statement.RepairDebuggingHandler;
import edu.stanford.bmir.protege.web.client.debugger.resources.DiffClientBundle;
import edu.stanford.bmir.protege.web.client.debugger.resources.Icon;
import edu.stanford.bmir.protege.web.shared.debugger.Diagnosis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public class StatementViewImpl extends Composite{



    interface StatementViewImplUiBinder extends UiBinder<HTMLPanel, StatementViewImpl> {

    }
    private static StatementViewImplUiBinder ourUiBinder = GWT.create(StatementViewImplUiBinder.class);

    private CheckCheckBoxHandler checkCheckBox = (clickEvent, checkBoxOther, list) -> {

    };
    FaultyAxiomRemoveHandler faultyAxiomRemoveHandler = axiom -> {

    };

    BackgroundAxiomRemoveHandler backgroundAxiomRemoveHandler = axiom -> {

    };

    DeleteTestCasesHandler deleteTestCasesHandler= axiom -> {

    };

    RepairDebuggingHandler repairDebuggingHandler = (diagnosis, index) -> {

    };

    @UiField
    protected FlexTable table;


    public List<RadioButton> radioGroups;

    protected StatementPresenter statementPresenter;

    public StatementViewImpl(StatementPresenter statementPresenter) {
        DiffClientBundle.INSTANCE.style().ensureInjected();
        this.statementPresenter = statementPresenter;
        initWidget(ourUiBinder.createAndBindUi(this));
        ColumnFormatter columnFormatter = table.getColumnFormatter();
        columnFormatter.setWidth(0,"95%");
    }

    List<SafeHtml> queryAxioms = new ArrayList<>();
    public void addQueriesStatement(Collection<SafeHtml> axiomStatement){
        List<CheckBox> listcheckbox= new ArrayList<>();
        for (SafeHtml axiom :
                axiomStatement) {
            int row = table.getRowCount();
            queryAxioms.add(axiom);
            Label statement = new HTML(axiom);
            CheckBox checkBoxP = new CheckBox();
            StringBuilder sbP = getStyle(Icon.TRUE);
            checkBoxP.setTitle("The axiom is correct");
            checkBoxP.setHTML(sbP.toString());
            CheckBox checkBoxN = new CheckBox();
            checkBoxN.setTitle("The axiom is incorrect");
            StringBuilder sbN = getStyle(Icon.FALSE);
            checkBoxN.setHTML(sbN.toString());
            listcheckbox.add(checkBoxP);
            listcheckbox.add(checkBoxN);

            checkBoxN.addClickHandler(clickEvent -> checkCheckBox.onClick(clickEvent,checkBoxP, listcheckbox));

            checkBoxP.addClickHandler(clickEvent -> checkCheckBox.onClick(clickEvent,checkBoxN, listcheckbox));
            table.setWidget(row,0,statement);
            table.setWidget(row,1, checkBoxP);
            table.setWidget(row,2, checkBoxN);
        }
    }

    private StringBuilder getStyle(Icon b){
        DiffClientBundle.DiffCssResource style = DiffClientBundle.INSTANCE.style();
        StringBuilder sb = new StringBuilder();
        if (b == Icon.TRUE){
            sb.append("<div class=\"").append( style.addBullet()).append(" \">").append("</div>");
        }else if( b == Icon.FALSE){
            sb.append("<div class=\"").append(style.removeBullet()).append(" \">").append("</div>");
        }else if ( b == Icon.BOTTOM){
            sb.append("<div class=\"").append(style.goBottom()).append(" \">").append("</div>");
        }else if (b == Icon.TOP){
            sb.append("<div class=\"").append(style.goTop()).append(" \">").append("</div>");
        }else if (b == Icon.CROSS){
            sb.append("<div class=\"").append(style.cross()).append(" \">").append("</div>");
        }else if (b == Icon.EDITOR){
            sb.append("<div class=\"").append(style.editor()).append(" \">").append("</div>");
        }
        return sb;
    }

    public void addRepairsStatement(List<Diagnosis> diagnoseStatement){
        int numOfRepairs = 1;
        for (Diagnosis diagnosis :
                diagnoseStatement) {
            int row = table.getRowCount();
            Button repairButton = new Button("R");
            StringBuilder sbN = getStyle(Icon.EDITOR);
            repairButton.setHTML(sbN.toString());
            int finalNumOfRepairs = numOfRepairs;
            repairButton.addClickHandler(clickEvent -> repairDebuggingHandler.handleRepairDebugging(diagnosis, finalNumOfRepairs -1));
            Label repair = new Label("Repair #"+(numOfRepairs++));
            repair.getElement().getStyle().setColor("blue");
            table.setWidget(row,0,repair);
            table.setWidget(row,1,repairButton);

            int numbAxioms = 1;
            for(SafeHtml axiom: diagnosis.getAxioms()){
                Label statement = new HTML(axiom);
                table.setWidget(row+numbAxioms,0,statement);
                numbAxioms++;
            }

        }
    }

    public void addTestcasesStatement(Set<SafeHtml> axiomStatement){
        for (SafeHtml axiom :
                axiomStatement) {
            int row = table.getRowCount();
            Label statement =new HTML(axiom);
            Button button = new Button("X");
            StringBuilder sbN = getStyle(Icon.CROSS);
            button.setTitle("Remove");
            button.setHTML(sbN.toString());
            table.setWidget(row, 0, statement);
            table.setWidget(row,1, button);
            button.addClickHandler(clickEvent -> deleteTestCasesHandler.DeleteTestCases(axiom));
        }
    }

//    List<SafeHtml> backgroundAxioms = new ArrayList<>();
//    List<SafeHtml> possibleFaultyAxioms = new ArrayList<>();

    public void updateBackground(List<SafeHtml> backgroundAxioms,List<SafeHtml> possibleFaultyAxioms, boolean isChecked) {
        table.clear();
        table.removeAllRows();
        if (!backgroundAxioms.isEmpty()) {
            for (SafeHtml axiom : backgroundAxioms) {
                int row = table.getRowCount();
                Label statement = new HTML(axiom);
                Button button = new Button("remove");
                button.setTitle("Remove from background");
                StringBuilder sbN = getStyle(Icon.TOP);
                button.setHTML(sbN.toString());

                table.setWidget(row, 0, statement);
                if (isChecked){table.setWidget(row, 1, button);}

                button.addClickHandler(clickEvent -> backgroundAxiomRemoveHandler.handlerBackgroundAxiomRemove(axiom));
            }
        }

        if (!possibleFaultyAxioms.isEmpty()) {
            for (SafeHtml axiom : possibleFaultyAxioms) {
                int row = table.getRowCount();
                Label statement = new HTML(axiom);
                Button button = new Button("remove");
                button.setTitle("Move to background");
                StringBuilder sbN = getStyle(Icon.BOTTOM);
                button.setHTML(sbN.toString());
                table.setWidget(row, 0, statement);
                if (isChecked){table.setWidget(row, 1, button);}

                button.addClickHandler(clickEvent -> faultyAxiomRemoveHandler.handlerFaultyAxiomRemove(axiom));
            }
        }
    }

    public void setFaultyAxiomRemoveHandler(FaultyAxiomRemoveHandler faultyAxiomRemoveHandler) {
        this.faultyAxiomRemoveHandler = faultyAxiomRemoveHandler;
    }

    public void setBackgroundAxiomRemoveHandler(BackgroundAxiomRemoveHandler backgroundAxiomRemoveHandler) {
        this.backgroundAxiomRemoveHandler = backgroundAxiomRemoveHandler;
    }

    public void setDeleteTestCasesHandler(DeleteTestCasesHandler deleteTestCasesHandler) {
        this.deleteTestCasesHandler = deleteTestCasesHandler;
    }

    public void setManchesterEditorHandler(RepairDebuggingHandler repairDebuggingHandler) {
        this. repairDebuggingHandler = repairDebuggingHandler;
    }
//
//    public void setDeleteRepairHandler(DeleteRepairHandler deleteRepairHandler) {
//        this.deleteRepairHandler = deleteRepairHandler;
//    }

    public void setCheckCheckBox(CheckCheckBoxHandler checkCheckBox) {
        this.checkCheckBox = checkCheckBox;
    }


}