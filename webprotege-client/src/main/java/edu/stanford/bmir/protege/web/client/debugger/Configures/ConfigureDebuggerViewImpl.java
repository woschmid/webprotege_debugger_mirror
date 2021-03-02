package edu.stanford.bmir.protege.web.client.debugger.Configures;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import edu.stanford.bmir.protege.web.client.library.text.ExpandingTextBoxImpl;

import javax.inject.Inject;

public class ConfigureDebuggerViewImpl extends Composite implements ConfigureDebuggerView{

    interface ConfigureDebuggerViewImplUiBinder extends UiBinder<HTMLPanel, ConfigureDebuggerViewImpl> {

    }

    private static ConfigureDebuggerViewImplUiBinder ourUiBinder = GWT.create(ConfigureDebuggerViewImplUiBinder.class);


    @Inject
    public ConfigureDebuggerViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}
