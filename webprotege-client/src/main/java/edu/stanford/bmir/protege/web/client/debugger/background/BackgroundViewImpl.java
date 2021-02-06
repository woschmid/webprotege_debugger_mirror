package edu.stanford.bmir.protege.web.client.debugger.background;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Jun 2018
 */
public class BackgroundViewImpl extends Composite implements BackgroundView {

    @UiField
    SimplePanel EntailedcriteriaContainer;

    @UiField
    SimplePanel NonEntailedcriteriaContainer;

    @UiField
    CheckBox ABox;

    @UiField
    CheckBox TBox;

    @UiField
    CheckBox RBox;

    @UiField
    Button allDown;

    @UiField
    Button allUp;

    private FilterAxiomsHandler filterAxiomsHandler = (isAbox, isTbox, isRbox) -> {

    };

    private MoveAllAxiom moveAllAxiom = () -> {

    };

    private ChangePage changePage = () -> {

    };

    @Override
    public AcceptsOneWidget getCriteriaContainer() {
        return EntailedcriteriaContainer;
    }

    interface repairsViewImplUiBinder extends UiBinder<HTMLPanel, BackgroundViewImpl> {

    }

    private static repairsViewImplUiBinder ourUiBinder = GWT.create(repairsViewImplUiBinder.class);


    @Inject
    public BackgroundViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ABox.setValue(true);

        TBox.setValue(true);
        RBox.setValue(true);
    }

    boolean isAbox = true;
    boolean isTbox = true;
    boolean isRbox = true ;

    @UiHandler("ABox")
    protected void ABoxButtonClick(ClickEvent event) {
        isAbox = ABox.getValue();
        filterAxiomsHandler.handleFilterAxioms(isAbox, isTbox, isRbox);
    }

    @UiHandler("TBox")
    protected void TBoxButtonClick(ClickEvent event) {
        isTbox = TBox.getValue();
        filterAxiomsHandler.handleFilterAxioms(isAbox, isTbox, isRbox);
    }

    @UiHandler("RBox")
    protected void RBoxButtonClick(ClickEvent event) {
        isRbox = RBox.getValue();
        filterAxiomsHandler.handleFilterAxioms(isAbox, isTbox, isRbox);
    }

    @UiHandler("allDown")
    protected void allDownButtonClick(ClickEvent event) {
        moveAllAxiom.handleMoveAllAxiom();
    }

    @UiHandler("allUp")
    protected void allUpButtonClick(ClickEvent event) {
        moveAllAxiom.handleMoveAllAxiom();
    }

    @UiHandler("lastPage")
    protected void LastPageButtonClick(ClickEvent event) {
        changePage.handleChangePage();
    }

    @UiHandler("nextPage")
    protected void NextPageButtonClick(ClickEvent event) {
        changePage.handleChangePage();
    }


    @UiHandler("helpButton")
    protected void helpButtonClick(ClickEvent event) { Window.open("https://git-ainf.aau.at/interactive-KB-debugging/debugger/-/wikis/input-ontology","_blank",""); }

    @Nonnull
    public AcceptsOneWidget getEntailedCriteriaContainer() {
        return EntailedcriteriaContainer;
    }

    @Nonnull
    public AcceptsOneWidget getNonEntailedcriteriaContainer() {
        return NonEntailedcriteriaContainer;
    }

    public void setFilterAxiomsHandler(@Nonnull FilterAxiomsHandler filterAxiomsHandler) {
        this.filterAxiomsHandler = filterAxiomsHandler;
    }
    public void setMoveAllAxiom(@Nonnull MoveAllAxiom moveAllAxiom) {
        this.moveAllAxiom = moveAllAxiom;
    }
    public void setChangePage(@Nonnull ChangePage changePage) {
        this.changePage = changePage;
    }
}