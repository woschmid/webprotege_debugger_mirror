package edu.stanford.bmir.protege.web.client.debugger.repairInterface;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.debugger.DebuggerView;
import edu.stanford.bmir.protege.web.client.debugger.resources.DiffClientBundle;
import edu.stanford.bmir.protege.web.client.debugger.resources.Icon;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Set;

import static com.google.gwt.dom.client.Style.TextDecoration.LINE_THROUGH;
import static com.google.gwt.dom.client.Style.TextDecoration.NONE;


public class RepairInterfaceViewImpl extends Composite implements RepairInterfaceView{


    @Override
    public SimplePanel getCriteriaContainer() {
        return null;
    }

    @Override
    public void setAxioms(Set<SafeHtml> axiomStatement) {

    }

    interface RepairInterfaceViewImplUiBinder extends UiBinder<HTMLPanel, RepairInterfaceViewImpl> {

    }
    private static RepairInterfaceViewImplUiBinder ourUiBinder = GWT.create(RepairInterfaceViewImplUiBinder.class);

    private DeleteRepairHandler deleteRepairHandler = selectedAxiom -> {

    };
    public ManchesterEditorHandler manchesterEditorHandler = (selectedAxiom, axiom, row, m, r) -> {

    };
    RedoRepairHandler redoRepairHandler = selectedAxiom -> {

    };

    @Inject
    public RepairInterfaceViewImpl(){
        DiffClientBundle.INSTANCE.style().ensureInjected();
        initWidget(ourUiBinder.createAndBindUi(this));
    }
    @UiField
    protected FlexTable table;

    public FlexTable getTable() {
        return table;
    }

    public void setAxioms(Collection<SafeHtml> axiomStatement){
        table.clear();
        for (SafeHtml axiom :
                axiomStatement) {
            int row = table.getRowCount();
            Label statement = new HTML(axiom);
            Button buttonM = new Button("Modify");
            buttonM.setTitle("Modify");
            Button buttonD = new Button("Delete");
            buttonD.setTitle("Delete");
            Button buttonR = new Button("Redo");
            buttonD.setTitle("Re");

            buttonM.addClickHandler(clickEvent -> manchesterEditorHandler.addManchesterEditor(axiom, statement.getText(), row,buttonM, buttonR));

            buttonD.addClickHandler(clickEvent -> {
                statement.getElement().getStyle().setTextDecoration(LINE_THROUGH);
                table.setWidget(row,0,statement);
                table.setWidget(row,1,buttonR);
                table.remove(buttonD);
                deleteRepairHandler.DeleteRepair(axiom);
            });

            buttonR.addClickHandler(clickEvent -> {
                statement.getElement().getStyle().setTextDecoration(NONE);
                table.remove(buttonR);
                table.setWidget(row,0,statement);
                table.setWidget(row,1, buttonM);
                table.setWidget(row,2, buttonD);
                redoRepairHandler.RedoRepair(axiom);
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

    public void changAxoim(String correctAxiom, int row, Button buttonM, Button buttonR){
        Label statement = new Label(correctAxiom);
        table.remove(buttonM);
        table.setWidget(row,0,statement);
        table.setWidget(row,3,buttonR);
    }

    public void setManchesterEditorHandler(ManchesterEditorHandler manchesterEditorHandler) {
        this.manchesterEditorHandler = manchesterEditorHandler;
    }

    public void setDeleteRepairHandler(DeleteRepairHandler deleteRepairHandler) {
        this.deleteRepairHandler = deleteRepairHandler;
    }

    public void setRedoRepairHandler(RedoRepairHandler redoRepairHandler){
        this.redoRepairHandler = redoRepairHandler;
    }
}
