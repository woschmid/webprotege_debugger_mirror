package edu.stanford.bmir.protege.web.server.shacl;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.debugger.Util;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.shacl.ShaclValidationResult;
import edu.stanford.bmir.protege.web.shared.shacl.ValidateShaclAction;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileUtils;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

public class ValidateShaclActionHandler extends AbstractProjectActionHandler<ValidateShaclAction, ShaclValidationResult> {

    @Nonnull
    private final RevisionManager revisionManager;

    @Inject
    public ValidateShaclActionHandler(@Nonnull AccessManager accessManager, @Nonnull RevisionManager revisionManager) {
        super(accessManager);
        this.revisionManager = checkNotNull(revisionManager);
    }

    @Nonnull
    @Override
    public Class<ValidateShaclAction> getActionClass() {
        return ValidateShaclAction.class;
    }

    @Nonnull
    @Override
    public ShaclValidationResult execute(@Nonnull ValidateShaclAction action, @Nonnull ExecutionContext executionContext) {
        return validate(action.getShaclEditorText());
    }

    private ShaclValidationResult validate(final String editorText) {
        try {
            List<Vector<String>> list = new ArrayList<>();
            JenaOwlConverter converter = new JenaOwlConverter();

            OWLOntology ont = InferredOntologyLoader.loadInferredOntology(revisionManager);
            Model dataModel = converter.convertOwlModelToJenaFormat(ont, FileUtils.langTurtle);

            // Load the main data model
            Model shapesModel = RdfModelReader.getModelFromString(editorText, FileUtils.langTurtle);
            final ShaclValidationReport report = ShaclValidation.runValidation(shapesModel, dataModel);
            List<ShaclValidationResultJena> validationResults = filterResults(report);
            validationResults.sort(ShaclValidationResultComparator.INSTANCE);

            // update table with result data
            for (ShaclValidationResultJena res : validationResults) {
                Vector<String> row = ShaclValidationResultJena.toRow(res);
                list.add(row);
            }
            return new ShaclValidationResult(list);

        }  catch (Exception e) {
            Util.logException(getActionClass(), e);
            List<Vector<String>> list = new ArrayList<>();
            Vector<String> v = new Vector();
            v.add(e.getMessage());
            list.add(v);
            return new ShaclValidationResult(list);
        }
    }

    private List<ShaclValidationResultJena> filterResults(ShaclValidationReport report) {
        Stream<ShaclValidationResultJena> results = report.validationResults.stream();
        results = results.filter(row -> row.focusNode != null && row.focusNode.isURIResource());
        return results.collect(Collectors.toList());
    }

}
