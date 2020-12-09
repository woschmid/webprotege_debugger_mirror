package edu.stanford.bmir.protege.web.client.debugger.testcases;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.debugger.resources.DiffClientBundle;
import edu.stanford.bmir.protege.web.client.debugger.resources.Icon;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Jun 2018
 */
public class TestcasesViewImpl extends Composite implements TestcasesView{

    @UiField
    SimplePanel EntailedcriteriaContainer;

    @UiField
    SimplePanel NonEntailedcriteriaContainer;

    @UiField
    protected Button addTestcasesButton;

    @UiField
    protected Button addNTestcasesButton;

    private AddtestcasesEHandler addtestcasesEHandler;

    private AddtestcasesNHandler addtestcasesNHandler;

    @Override
    public AcceptsOneWidget getCriteriaContainer() {
        return EntailedcriteriaContainer;
    }

    interface repairsViewImplUiBinder extends UiBinder<HTMLPanel, TestcasesViewImpl> {

    }

    private static repairsViewImplUiBinder ourUiBinder = GWT.create(repairsViewImplUiBinder.class);


    @Inject
    public TestcasesViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
        StringBuilder sbP = getStyle(Icon.ADD);
        addTestcasesButton.setTitle("Add entailed Testcase");
        addTestcasesButton.setHTML(sbP.toString());
        StringBuilder sbN = getStyle(Icon.NADD);
        addNTestcasesButton.setTitle("Add Non-entailed Testcase");
        addNTestcasesButton.setHTML(sbN.toString());
    }

    @UiHandler("helpButton")
    protected void helpButtonClick(ClickEvent event) { Window.open("https://git-ainf.aau.at/interactive-KB-debugging/debugger/-/wikis/acquired-test-cases","_blank",""); }

    @UiHandler("addTestcasesButton")
    protected void addTestcasesButtonClick(ClickEvent event) { addtestcasesEHandler.addTestcases(); }

    @UiHandler("addNTestcasesButton")
    protected void addNTestcasesButtonClick(ClickEvent event) { addtestcasesNHandler.addTestcases(); }

    @Nonnull
    public AcceptsOneWidget getEntailedCriteriaContainer() {
        return EntailedcriteriaContainer;
    }

    @Nonnull
    public AcceptsOneWidget getNonEntailedcriteriaContainer() {
        return NonEntailedcriteriaContainer;
    }

    @Override
    public void setAddTestcasesEHandler(AddtestcasesEHandler addTestcasesHandler) {
        this.addtestcasesEHandler = addTestcasesHandler;
    }

    @Override
    public void setAddTestcasesNHandler(AddtestcasesNHandler addTestcasesHandler) {
        this.addtestcasesNHandler = addTestcasesHandler;
    }

//    @Override
//    public void setAddTestcasesHandler(AddtestcasesHandler addtestcasesHandler) {
//        this.addtestcasesHandler = addtestcasesHandler;
//    }

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
        }else if (b == Icon.NADD){
            sb.append("<div class=\"").append(style.addNTest()).append(" \">").append("</div>");
        } else if (b == Icon.ADD){
            sb.append("<div class=\"").append(style.addTest()).append(" \">").append("</div>");
        }
        return sb;
    }

}