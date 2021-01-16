package edu.stanford.bmir.protege.web.client.debugger.queries;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.resources.WebProtegeClientBundle.BUNDLE;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 16/02/16
 */
public class QueriesViewImpl extends Composite implements QueriesView {
    @UiField
    SimplePanel criteriaContainer;

//    @UiField
//    protected Button startButton;

    String CHECK="Check";

    String START="Debug";

    @UiField
    protected Button editButton;

    @UiField
    protected Button helpButton;

    @UiField
    protected Button startButton;

    @UiField
    protected Button submitButton;

    @UiField
    protected Button stopButton;

    interface QeriesViewImplUiBinder extends UiBinder<HTMLPanel, QueriesViewImpl> {

    }

    private static QeriesViewImplUiBinder ourUiBinder = GWT.create(QeriesViewImplUiBinder.class);

    private StartDebuggingHandler startDebuggingHandler = () -> {
    };

    private StopDebuggingHandler stopDebuggingHandler = () -> {
    };

    private SubmitDebuggingHandler submitDebuggingHandler = () -> {
    };

    private CheckOntologyHandler checkOntologyHandler = () -> {};

    private EditSettingHandler editSettingHandler = () -> {};

    private HelpHandler helpHandler = () -> {};

    @Inject
    public QueriesViewImpl() { initWidget(ourUiBinder.createAndBindUi(this)); }

    @UiHandler("startButton")
    protected void handleStartDebugging(ClickEvent clickEvent) {
        if (startButton.getText().equals(CHECK)){
            // TODO: 2020/10/27 pre-processing handler
            checkOntologyHandler.handlerCheckontology();
            changeStartButton(true);
        }else{
            startDebuggingHandler.handleStartDebugging();
            changeStartButton(false);
        }

    }

    public void changeStartButton(boolean start){
        if (start){
            // TODO: 2020/10/27 pre-processing handler
            startButton.setText(START);
            startButton.setTitle("Start Debugger");
        }else{
            startButton.setText(CHECK);
            startButton.setTitle("Check Ontology");
        }
    }
    @UiHandler("editButton")
    protected void handleEditSetting(ClickEvent clickEvent){
        editSettingHandler.EditSetting();
    }

    @UiHandler("helpButton")
    protected void handleHelp(ClickEvent clickEvent){
        helpHandler.showHelp();
    }

    @UiHandler("stopButton")
    protected void handleStopDebugging(ClickEvent clickEvent){
        stopDebuggingHandler.handleStopDebugging();
    }

    @UiHandler("submitButton")
    protected void submitButtonClick(ClickEvent event) { submitDebuggingHandler.handleSubmitDebugging(); }

    @Override
    public void setStartDebuggingHandler(@Nonnull StartDebuggingHandler handler) { this.startDebuggingHandler = checkNotNull(handler); }

    @Override
    public void setStopDebuggingHandler(@Nonnull StopDebuggingHandler handler) { this.stopDebuggingHandler = checkNotNull(handler);}

    @Override
    public void setSubmitDebuggingHandler(@Nonnull SubmitDebuggingHandler handler){ this.submitDebuggingHandler = checkNotNull(handler);}

    @Override
    public void setCheckOntologyHandler(CheckOntologyHandler checkOntologyHandler) { this.checkOntologyHandler = checkOntologyHandler; }

    @Override
    public void setEditSettingHandler(@Nonnull EditSettingHandler editSettingHandler) { this.editSettingHandler = editSettingHandler; }

    @Override
    public void setHelpHandler(@Nonnull HelpHandler helpHandler) { this.helpHandler = helpHandler; }

    @Nonnull
    public AcceptsOneWidget getCriteriaContainer() {
        return criteriaContainer;
    }

    @Override
    public void disablebutton(String s) {
        switch (s){
            case "start":
                startButton.setEnabled(false);
                break;
            case "stop":
                stopButton.setEnabled(false);
                break;
            case "submit":
                submitButton.setEnabled(false);
                break;
        }
    }

    public void enablebutton(String s) {
        switch (s){
            case "start":
                startButton.setEnabled(true);
                break;
            case "stop":
                stopButton.setEnabled(true);
                break;
            case "submit":
                submitButton.setEnabled(true);
                break;
        }
    }

}