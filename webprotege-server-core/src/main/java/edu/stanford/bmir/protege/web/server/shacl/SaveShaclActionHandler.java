package edu.stanford.bmir.protege.web.server.shacl;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.shacl.SaveShaclAction;
import edu.stanford.bmir.protege.web.shared.shacl.ShaclValidationResult;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.google.common.base.Preconditions.checkNotNull;

public class SaveShaclActionHandler extends AbstractProjectActionHandler<SaveShaclAction, ShaclValidationResult> {

    @Nonnull
    private final RevisionManager revisionManager;

    @Inject
    public SaveShaclActionHandler(@Nonnull AccessManager accessManager, @Nonnull RevisionManager revisionManager) {
        super(accessManager);
        this.revisionManager = checkNotNull(revisionManager);
    }

    @Nonnull
    @Override
    public Class<SaveShaclAction> getActionClass() {
        return SaveShaclAction.class;
    }

    @Nonnull
    @Override
    public ShaclValidationResult execute(@Nonnull SaveShaclAction action, @Nonnull ExecutionContext executionContext) {
        // todo save
        List<Vector<String>> list = new ArrayList<>();
        Vector<String> row = new Vector<>();
        row.add("Saved successfully");
        list.add(row);
        return new ShaclValidationResult(list);
    }

}
