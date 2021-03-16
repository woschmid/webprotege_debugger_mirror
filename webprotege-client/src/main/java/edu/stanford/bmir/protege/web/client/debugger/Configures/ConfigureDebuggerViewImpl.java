package edu.stanford.bmir.protege.web.client.debugger.Configures;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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

    public void setLimitToInput(){
        addKeyPressHandler(DSKTtextBox);
        addKeyPressHandler(RTtextBox);
        addKeyPressHandler(MVPFAtextBox);
        addKeyPressHandler(MVCAtextBox);
    }

    private void addKeyPressHandler(TextBox textBox){
        textBox.addKeyPressHandler(event -> {
            String input = textBox.getText();
            String rest = input.substring(0,input.length() - 1);
            textBox.setText(rest);
            String last = input.substring(input.length() - 1);
            if (last.matches("[0-9]")) {
                textBox.setText(input);
            }
        });
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
}
