package edu.stanford.bmir.protege.web.client.debugger.statement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

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

    public void startQueriesStatement(){
        table.addStyleName("style.searchBox");
        table.getColumnFormatter().setWidth(0, "400px");
        table.getColumnFormatter().setWidth(1, "50px");
        table.getColumnFormatter().setWidth(2, "50px");
        int row = table.getRowCount();
        Label statement = new Label("statement");
        RadioButton rb0 = new RadioButton("myRadioGroup", "+");
        RadioButton rb1 = new RadioButton("myRadioGroup", "-");
        table.setWidget(row,0,statement);
        table.setWidget(row,1,rb0);
        table.setWidget(row,2,rb1);
    }
    public void addQueriesStatement(){
        table.addStyleName("style.searchBox");
        table.getColumnFormatter().setWidth(0, "400px");
        table.getColumnFormatter().setWidth(1, "50px");
        table.getColumnFormatter().setWidth(2, "50px");
        int row = table.getRowCount();
        Label statement = new Label("statement");
        RadioButton rb0 = new RadioButton("myRadioGroup", "+");
        RadioButton rb1 = new RadioButton("myRadioGroup", "-");
        table.setWidget(row,0,statement);
        table.setWidget(row,1,rb0);
        table.setWidget(row,2,rb1);
    }

    public void startRepairsStatement(){
        table.addStyleName("style.searchBox");
        table.getColumnFormatter().setWidth(0, "400px");
        int row = table.getRowCount();
        Label repair = new Label("Repair #"+(row+1));
        Label statement = new Label("statement");
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

    public void startTestcasesStatement(){
        table.addStyleName("style.searchBox");
        table.getColumnFormatter().setWidth(0, "400px");
        table.getColumnFormatter().setWidth(1, "50px");
        int row = table.getRowCount();
        Label statement = new Label("statement");
        Button rb0 = new Button("X");
        table.setWidget(row,0,statement);
        table.setWidget(row,1,rb0);
    }

}