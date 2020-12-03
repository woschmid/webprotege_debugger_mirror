package edu.stanford.bmir.protege.web.client.debugger.repairInterface;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.debugger.resources.DiffClientBundle;
import edu.stanford.bmir.protege.web.client.debugger.resources.Icon;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class RepairInterfaceViewImpl extends Composite{



    interface RepairInterfaceViewImplUiBinder extends UiBinder<HTMLPanel, RepairInterfaceViewImpl> {

    }
    private static RepairInterfaceViewImplUiBinder ourUiBinder = GWT.create(RepairInterfaceViewImplUiBinder.class);

    private DeleteRepairHandler deleteRepairHandler = new DeleteRepairHandler() {
        @Override
        public void DeleteRepair(SafeHtml selectedAxiom) {

        }
    };
    ManchesterEditorHandler manchesterEditorHandler = new ManchesterEditorHandler() {
        @Override
        public void addManchesterEditor() {

        }
    };

    public RepairInterfaceViewImpl(){
        DiffClientBundle.INSTANCE.style().ensureInjected();
        initWidget(ourUiBinder.createAndBindUi(this));
    }
    @UiField
    protected FlexTable table;

    public FlexTable getTable() {
        return table;
    }

    public void setAxioms(Set<SafeHtml> axiomStatement){
        table.clear();
        for (SafeHtml axiom :
                axiomStatement) {
            int row = table.getRowCount();
            Label statement = new HTML(axiom);
            Button buttonM = new Button("Modify");
//            StringBuilder sbP = getStyle(Icon.TRUE);
            buttonM.setTitle("Modify");
//            buttonM.setHTML(sbP.toString());
            Button buttonD = new Button("Delete");
            buttonD.setTitle("Delete");
//            StringBuilder sbN = getStyle(Icon.FALSE);
//            buttonD.setHTML(sbN.toString());

            buttonM.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    manchesterEditorHandler.addManchesterEditor();
                }
            });

            buttonD.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    deleteRepairHandler.DeleteRepair(axiom);
                }
            });
            table.setWidget(row,0,statement);
            table.setWidget(row,1, buttonM);
            table.setWidget(row,2, buttonD);
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

    public void setManchesterEditorHandler(ManchesterEditorHandler manchesterEditorHandler) {
        this.manchesterEditorHandler = manchesterEditorHandler;
    }

    public void setDeleteRepairHandler(DeleteRepairHandler deleteRepairHandler) {
        this.deleteRepairHandler = deleteRepairHandler;
    }
}
