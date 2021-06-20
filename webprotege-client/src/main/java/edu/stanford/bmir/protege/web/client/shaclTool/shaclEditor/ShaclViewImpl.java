package edu.stanford.bmir.protege.web.client.shaclTool.shaclEditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.debugger.queries.QueriesViewImpl;

import javax.annotation.Nonnull;
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


    @Inject
    public ShaclViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}