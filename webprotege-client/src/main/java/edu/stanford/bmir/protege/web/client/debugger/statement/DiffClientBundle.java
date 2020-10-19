package edu.stanford.bmir.protege.web.client.debugger.statement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29/01/15
 */
public interface DiffClientBundle extends ClientBundle {

    DiffClientBundle INSTANCE = GWT.create(DiffClientBundle.class);

    @Source("../resources/diff.css")
    DiffCssResource style();

    @Source("../resources/change-op-add-icon.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource changeOpAddIcon();

    @Source("../resources/change-op-remove-icon.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource changeOpRemoveIcon();

    @Source("../resources/move-to-child.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource moveToChildIcon();

    @Source("../resources/move-to-parent.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource moveToParentIcon();

    @Source("../resources/cross.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource crossIcon();

    interface DiffCssResource extends CssResource {

        String line();

        String add();

        String addBullet();

        String remove();

        String removeBullet();

        String source();

        String goTop();

        String goBottom();

        String cross();

        String lineElement();
    }
}
