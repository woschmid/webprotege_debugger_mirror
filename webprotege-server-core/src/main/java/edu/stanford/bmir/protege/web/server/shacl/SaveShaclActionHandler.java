package edu.stanford.bmir.protege.web.server.shacl;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.debugger.Util;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.shacl.SaveShaclAction;
import edu.stanford.bmir.protege.web.shared.shacl.ShaclValidationResult;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileUtils;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
        return save(action.getShaclEditorText());
    }

    private ShaclValidationResult save(final String editorText) {
        List<Vector<String>> list = new ArrayList<>();
        Vector<String> row = new Vector<>();

        if (editorText == null || "".equals(editorText.trim())) {
            row.add("Empty editor text!");
            list.add(row);
            return new ShaclValidationResult(list);
        }

        InferredOntologyLoader inferredOntologyLoader = null;
        try {

            // validation
            JenaOwlConverter converter = new JenaOwlConverter();
            inferredOntologyLoader = new InferredOntologyLoader();
            OWLOntology ontology = inferredOntologyLoader.loadInferredOntology(revisionManager);
            Model dataModel = converter.convertOwlModelToJenaFormat(ontology, FileUtils.langTurtle);
            // Load the main data model
            Model shapesModel = RdfModelReader.getModelFromString(editorText, FileUtils.langTurtle);
            final ShaclValidationReport report = ShaclValidation.runValidation(shapesModel, dataModel);

            // validation successful -> save
            Set<OWLAnnotation> annotations = ontology.getAnnotations();
            final OWLOntologyManager owlOntologyManager = ontology.getOWLOntologyManager();
            final OWLDataFactory df = owlOntologyManager.getOWLDataFactory();

            row.add("Saved successfully");
            list.add(row);
            return new ShaclValidationResult(list);
        } catch (Exception e) {
            Util.logException(getActionClass(), e);
            row.add(e.getMessage());
            list.add(row);
            return new ShaclValidationResult(list);
        } finally {
            if (inferredOntologyLoader != null)
                inferredOntologyLoader.dispose();
        }
    }

}
