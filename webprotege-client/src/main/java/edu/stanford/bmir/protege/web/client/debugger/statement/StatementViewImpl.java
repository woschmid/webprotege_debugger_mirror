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
import edu.stanford.bmir.protege.web.client.debugger.statement.DeleteRepairHandler;
import edu.stanford.bmir.protege.web.client.debugger.statement.ManchesterEditorHandler;
import edu.stanford.bmir.protege.web.client.debugger.resources.DiffClientBundle;
import edu.stanford.bmir.protege.web.client.debugger.resources.Icon;
import edu.stanford.bmir.protege.web.shared.debugger.Diagnosis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class StatementViewImpl extends Composite{

    interface StatementViewImplUiBinder extends UiBinder<HTMLPanel, StatementViewImpl> {

    }
    private static StatementViewImplUiBinder ourUiBinder = GWT.create(StatementViewImplUiBinder.class);

    private CheckCheckBoxHandler checkCheckBox = new CheckCheckBoxHandler() {
        @Override
        public void onClick(ClickEvent clickEvent, CheckBox checkBoxOther, List<CheckBox> list) {

        }
    };
    FaultyAxiomRemoveHandler faultyAxiomRemoveHandler = new FaultyAxiomRemoveHandler() {
        @Override
        public void handlerFaultyAxiomRemove(SafeHtml axiom) {

        }
    };

    BackgroundAxiomRemoveHandler backgroundAxiomRemoveHandler = new BackgroundAxiomRemoveHandler() {
        @Override
        public void handlerBackgroundAxiomRemove(SafeHtml axiom) {

        }
    };

    DeleteTestCasesHandler deleteTestCasesHandler= new DeleteTestCasesHandler() {
        @Override
        public void DeleteTestCases(SafeHtml axiom) {

        }
    };

//    private DeleteRepairHandler deleteRepairHandler = new DeleteRepairHandler() {
//        @Override
//        public void DeleteRepair(SafeHtml selectedAxiom) {
//
//        }
//    };
//
//    ManchesterEditorHandler manchesterEditorHandler = new ManchesterEditorHandler() {
//        @Override
//        public void addManchesterEditor() {
//
//        }
//    };



    @UiField
    protected FlexTable table;


    public List<RadioButton> radioGroups;

    protected StatementPresenter statementPresenter;

    public StatementViewImpl(StatementPresenter statementPresenter) {
        DiffClientBundle.INSTANCE.style().ensureInjected();
        this.statementPresenter = statementPresenter;
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    List<SafeHtml> queryAxioms = new ArrayList<>();
    public void addQueriesStatement(Set<SafeHtml> axiomStatement){
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
        }
        return sb;
    }

    public void addRepairsStatement(List<Diagnosis> diagnoseStatement){
        int numOfRepairs = 1;
        for (Diagnosis diagnosis :
                diagnoseStatement) {
            int row = table.getRowCount();
            Label repair = new Label("Repair #"+(numOfRepairs++));
            repair.getElement().getStyle().setColor("blue");
            table.setWidget(row,0,repair);
            int numbAxioms = 1;
            for(SafeHtml axiom: diagnosis.getAxioms()){
                Label statement = new HTML(axiom);
                table.setWidget(row+numbAxioms,0,statement);
//                if (diagnoseStatement.size()== 1){
//                    Button buttonM = new Button("Modify");
//                    buttonM.setTitle("Modify");
//                    Button buttonD = new Button("Delete");
//                    buttonD.setTitle("Delete");
//                    buttonM.addClickHandler(new ClickHandler() {
//                        @Override
//                        public void onClick(ClickEvent clickEvent) {
//                            manchesterEditorHandler.addManchesterEditor();
//                        }
//                    });
//
//                    buttonD.addClickHandler(new ClickHandler() {
//                        @Override
//                        public void onClick(ClickEvent clickEvent) {
//                            deleteRepairHandler.DeleteRepair(axiom);
//                        }
//                    });
//                    table.setWidget(row+numbAxioms,1,buttonM);
//                    table.setWidget(row+numbAxioms,2,buttonD);
//                }
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
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    deleteTestCasesHandler.DeleteTestCases(axiom);
                }
            });
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

                button.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        backgroundAxiomRemoveHandler.handlerBackgroundAxiomRemove(axiom);
                    }
                });
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

                button.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        faultyAxiomRemoveHandler.handlerFaultyAxiomRemove(axiom);
                    }
                });
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

//    public void setManchesterEditorHandler(ManchesterEditorHandler manchesterEditorHandler) {
//        this.manchesterEditorHandler = manchesterEditorHandler;
//    }
//
//    public void setDeleteRepairHandler(DeleteRepairHandler deleteRepairHandler) {
//        this.deleteRepairHandler = deleteRepairHandler;
//    }

    public void setCheckCheckBox(CheckCheckBoxHandler checkCheckBox) {
        this.checkCheckBox = checkCheckBox;
    }


}