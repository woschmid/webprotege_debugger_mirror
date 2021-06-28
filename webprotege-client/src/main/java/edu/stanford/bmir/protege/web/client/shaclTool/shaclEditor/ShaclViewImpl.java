package edu.stanford.bmir.protege.web.client.shaclTool.shaclEditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Jun 2018
 */
public class ShaclViewImpl extends Composite implements ShaclView {



    interface ShaclViewImplUiBinder extends UiBinder<HTMLPanel, ShaclViewImpl> {

    }

    private static ShaclViewImplUiBinder ourUiBinder = GWT.create(ShaclViewImplUiBinder.class);

    @UiField
    protected Button openButton;

    @UiField
    protected Button saveButton;

    @UiField
    protected Button runButton;

    @UiField
    protected Button editButton;

    @UiField
    protected Button helpButton;

    @UiField
    protected TextArea ContentArea;

    private ShaclValidateHandler shaclValidateHandler = () -> {
    };

    private ShaclEditHandler shaclEditHandler = () -> {
    };

    private ShaclHelpHandler shaclHelpHandler = () -> {
    };

    private ShaclOpenHandler shaclOpenHandler = () -> {
    };

    private ShaclSaveHandler shaclSaveHandler = () -> {
    };

    @Inject
    public ShaclViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @UiHandler("runButton")
    protected void handleValidate(ClickEvent clickEvent) {
        GWT.log("[QueriesViewImpl]handleValidate Button pressed!!!!!");
        shaclValidateHandler.handlerValidate();
    }

    @UiHandler("openButton")
    protected void handleOpen(ClickEvent clickEvent) {
        GWT.log("[QueriesViewImpl]handleOpen Button pressed!!!!!");
        shaclOpenHandler.handlerOpen();
    }

    @UiHandler("saveButton")
    protected void handleSave(ClickEvent clickEvent) {
        GWT.log("[QueriesViewImpl]handleSave Button pressed!!!!!");
        shaclSaveHandler.handlerSave();
    }

    @UiHandler("editButton")
    protected void handleEdit(ClickEvent clickEvent) {
        GWT.log("[QueriesViewImpl]handleEdit Button pressed!!!!!");
        shaclEditHandler.handlerEdit();
    }

    @UiHandler("helpButton")
    protected void handlehelp(ClickEvent clickEvent) {
        GWT.log("[QueriesViewImpl]helpButton Button pressed!!!!!");
        shaclHelpHandler.handlerHelp();
    }

    public String getContent() {
        return ContentArea.getText();
    }

    public void setShaclValidateHandler(ShaclValidateHandler shaclValidateHandler) {
        this.shaclValidateHandler = shaclValidateHandler;
    }

    public void setShaclEditHandler(ShaclEditHandler shaclEditHandler) {
        this.shaclEditHandler = shaclEditHandler;
    }

    public void setShaclHelpHandler(ShaclHelpHandler shaclHelpHandler) {
        this.shaclHelpHandler = shaclHelpHandler;
    }

    public void setShaclOpenHandler(ShaclOpenHandler shaclOpenHandler) {
        this.shaclOpenHandler = shaclOpenHandler;
    }

    public void setShaclSaveHandler(ShaclSaveHandler shaclSaveHandler) {
        this.shaclSaveHandler = shaclSaveHandler;
    }
}