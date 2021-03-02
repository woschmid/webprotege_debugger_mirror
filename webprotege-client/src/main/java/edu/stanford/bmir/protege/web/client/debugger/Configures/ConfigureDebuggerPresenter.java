package edu.stanford.bmir.protege.web.client.debugger.Configures;

import edu.stanford.bmir.protege.web.shared.debugger.Preferences;

public class ConfigureDebuggerPresenter {

    ConfigureDebuggerViewImpl configureDebuggerView;

    Preferences preferences = new Preferences();

    public ConfigureDebuggerPresenter(ConfigureDebuggerViewImpl configureDebuggerView){
        this.configureDebuggerView=configureDebuggerView;
    }

}
