package edu.stanford.bmir.protege.web.client.debugger.Configures;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.library.text.ExpandingTextBoxImpl;
import edu.stanford.bmir.protege.web.shared.debugger.Preferences;

import javax.inject.Inject;

public class ConfigureDebuggerViewImpl extends Composite implements ConfigureDebuggerView{


    @UiField
    protected TextBox DSKTtextBox;

    @UiField
    protected TextBox RTtextBox;

    @UiField
    protected TextBox MVPFAtextBox;

    @UiField
    protected TextBox MVCAtextBox;

    @UiField
    protected ListBox ReseanerFild;

    @UiField
    protected TextBox MNODtextBox;


    @Override
    public AcceptsOneWidget getCriteriaContainer() {
        return null;
    }

    interface ConfigureDebuggerViewImplUiBinder extends UiBinder<HTMLPanel, ConfigureDebuggerViewImpl> {

    }

    private static ConfigureDebuggerViewImplUiBinder ourUiBinder = GWT.create(ConfigureDebuggerViewImplUiBinder.class);


    @Inject
    public ConfigureDebuggerViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }


    public String getDSKTtextBox() {
        return DSKTtextBox.getText();
    }

    public String getRTtextBox() {
        return RTtextBox.getText();
    }

    public String getMVPFAtextBox() {
        return MVPFAtextBox.getText();
    }

    public String getMVCAtextBox() {
        return MVCAtextBox.getText();
    }

    public String getReseanerFild() {
        return ReseanerFild.getSelectedItemText();
    }

    public String getMNODtextBox() {
        return MNODtextBox.getText();
    }

}
