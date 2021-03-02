package edu.stanford.bmir.protege.web.client.debugger.background;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.pagination.HasPagination;
import edu.stanford.bmir.protege.web.client.pagination.PaginatorPresenter;
import edu.stanford.bmir.protege.web.client.pagination.PaginatorView;
import edu.stanford.bmir.protege.web.client.search.SearchStringChangedHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Jun 2018
 */
public class BackgroundViewImpl extends Composite implements BackgroundView {


    private final PaginatorPresenter paginatorPPresenter;

    private final PaginatorPresenter paginatorCPresenter;

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
    Label PFANumber;

    @UiField
    Label CANumber;

    @UiField
    protected TextBox searchBox;

//    @UiField
//    Button allDown;
//
//    @UiField
//    Button allUp;

    @UiField(provided = true)
    protected PaginatorView paginatorP;

    @UiField(provided = true)
    protected PaginatorView paginatorC;

    private FilterAxiomsHandler filterAxiomsHandler = (isAbox, isTbox, isRbox) -> {};

    private MoveAllAxiom moveAllAxiom = (boolean down) -> {};

    private ChangePage changePage = (int step) -> {};

    private SearchStringChangedHandler searchStringChangedHandler = () -> {};

    @Override
    public AcceptsOneWidget getCriteriaContainer() {
        return EntailedcriteriaContainer;
    }

    interface repairsViewImplUiBinder extends UiBinder<HTMLPanel, BackgroundViewImpl> {

    }

    private static repairsViewImplUiBinder ourUiBinder = GWT.create(repairsViewImplUiBinder.class);


    @Inject
    public BackgroundViewImpl(PaginatorPresenter paginatorPresenterP, PaginatorPresenter paginatorCPresenter) {

        this.paginatorPPresenter = paginatorPresenterP;
        this.paginatorCPresenter = paginatorCPresenter;
        paginatorP = paginatorPPresenter.getView();
        paginatorC = paginatorCPresenter.getView();
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
        moveAllAxiom.handleMoveAllAxiom(true);
    }

    @UiHandler("allUp")
    protected void allUpButtonClick(ClickEvent event) {
        moveAllAxiom.handleMoveAllAxiom(false);
    }

    @UiHandler("searchBox")
    protected void handleSearchStringChanged(KeyUpEvent event) {
        searchStringChangedHandler.handleSearchStringChanged();
    }

    @Override
    public String getSearchString() {
        return searchBox.getText();
    }

    @Override
    public void clearSearchString() {
        searchBox.setText("");
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

    @Override
    public void setPageCountP(int pageCount) {
        paginatorPPresenter.setPageCount(pageCount);
    }

    @Override
    public void setPageNumberP(int pageNumber) {
        paginatorPPresenter.setPageNumber(pageNumber);
    }

    @Override
    public int getPageNumberP() {
        return paginatorPPresenter.getPageNumber();
    }

    @Override
    public void setPageNumberChangedHandlerP(HasPagination.PageNumberChangedHandler handler) {
        paginatorPPresenter.setPageNumberChangedHandler(handler);
    }

    @Override
    public void setPageCountC(int pageCount) {
        paginatorCPresenter.setPageCount(pageCount);
    }

    @Override
    public void setPageNumberC(int pageNumber) {
        paginatorCPresenter.setPageNumber(pageNumber);
    }

    @Override
    public int getPageNumberC() {
        return paginatorCPresenter.getPageNumber();
    }

    @Override
    public void setPageNumberChangedHandlerC(HasPagination.PageNumberChangedHandler handler) {
        paginatorCPresenter.setPageNumberChangedHandler(handler);
    }

    @Override
    public void setPFANumber(int number) {
        PFANumber.setText(number+"  Axioms");
    }

    @Override
    public void setCANumber(int number) {
        CANumber.setText(number+"   Axioms");
    }

    @Override
    public void setSearchStringChangedHandler(@Nonnull SearchStringChangedHandler handler) {
        searchStringChangedHandler = checkNotNull(handler);
    }

}