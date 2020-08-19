package edu.stanford.bmir.protege.web.client.debugger.statement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.Set;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 16/02/16
 */
public class StatementViewImpl extends Composite{



    interface QeriesViewImplUiBinder extends UiBinder<HTMLPanel, StatementViewImpl> {

    }

    private static QeriesViewImplUiBinder ourUiBinder = GWT.create(QeriesViewImplUiBinder.class);

    @UiField
    protected FlexTable table;

    protected StatementPresenter statementPresenter;

    public StatementViewImpl(StatementPresenter statementPresenter) {
        this.statementPresenter = statementPresenter;
        initWidget(ourUiBinder.createAndBindUi(this));
        table.addStyleName("style.searchBox");
        table.getColumnFormatter().setWidth(0, "400px");
        table.getColumnFormatter().setWidth(1, "50px");
        table.getColumnFormatter().setWidth(2, "50px");
    }

    public void addQueriesStatement(Set<String> axiomStatement){
        for (String axiom :
                axiomStatement) {
            int row = table.getRowCount();
            String radioGroup = axiom+"RadioGroup";
            Label statement = new Label(axiom);
            RadioButton rb0 = new RadioButton(radioGroup, "+");
            RadioButton rb1 = new RadioButton(radioGroup, "-");
            table.setWidget(row,0,statement);
            table.setWidget(row,1,rb0);
            table.setWidget(row,2,rb1);
        }

    }
    public void addRepairsStatement(Set<String> axiomStatement){
        int numOfRepairs = 1;
        for (String axiom :
                axiomStatement) {
            int row = table.getRowCount();
            Label repair = new Label("Repair #"+(numOfRepairs++));
            Label statement = new Label(axiom);
            Button rb0 = new Button("Fix");
            rb0.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    Window.alert("Do fixing");
                }
            });
            rb0.setStyleName("button");
            table.setWidget(row,0,repair);
            table.setWidget(row,1,rb0);
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

}