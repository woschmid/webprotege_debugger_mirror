package edu.stanford.bmir.protege.web.client.shaclTool.shaclResultTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.debugger.resources.Icon;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class ShaclResultTableViewImpl extends Composite {
    interface ShaclResultTabelViewImplUiBinder extends UiBinder<HTMLPanel, ShaclResultTableViewImpl> {

    }

    private static ShaclResultTabelViewImplUiBinder ourUiBinder = GWT.create(ShaclResultTabelViewImplUiBinder.class);

    @UiField
    protected FlexTable table;

    @Inject
    public ShaclResultTableViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setTableTitle();
    }

    public void addToTable(List<Vector<String>> data){
        for (Vector<String> d:  data
             ) {
            GWT.log("[ShaclResultTableViewImpl] "+d.toString());
            int row = table.getRowCount();
            int index = 0;
            for (String v :
                    d) {
                Label col1 = new Label(v);
                table.setWidget(row,index++,col1);
            }
        }
    }
    public void setTableTitle(){
        table.clear();
        Label col1 = new Label("Severity");
        Label col2 = new Label("SourceShape");
        Label col3 = new Label("Message");
        Label col4 = new Label("FocusNode");
        Label col5 = new Label("Path");
        Label col6 = new Label("Value");
        table.setWidget(0, 0, col1);
        table.setWidget(0 ,1, col2);
        table.setWidget(0, 2, col3);
        table.setWidget(0 ,3, col4);
        table.setWidget(0, 4, col5);
        table.setWidget(0 ,5, col6);
    }
}
