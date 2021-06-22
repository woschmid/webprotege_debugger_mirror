package edu.stanford.bmir.protege.web.server.shacl;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.shared.shacl.ShaclValidationResult;
import edu.stanford.bmir.protege.web.shared.shacl.ValidateShaclAction;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ValidateShaclActionHandler extends AbstractProjectActionHandler<ValidateShaclAction, ShaclValidationResult> {

    @Inject
    public ValidateShaclActionHandler(@Nonnull AccessManager accessManager) {
        super(accessManager);
    }

    @Nonnull
    @Override
    public Class<ValidateShaclAction> getActionClass() {
        return ValidateShaclAction.class;
    }

    @Nonnull
    @Override
    public ShaclValidationResult execute(@Nonnull ValidateShaclAction action, @Nonnull ExecutionContext executionContext) {
        // TODO
        String shaclEditorText = action.getShaclEditorText();
        return new ShaclValidationResult(convertResult());
    }

    private List<Vector<String>> convertResult() {
        List<Vector<String>> list = new ArrayList<>();
        Vector<String> v = new Vector();
        v.add("Hallo");
        v.add("Boda");
        list.add(v);
        return list;
    }
}
