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
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
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
        return validate(action.getShaclEditorText(), action.getOwlEntity());
    }

    private ShaclValidationResult validate(final String editorText, @Nullable OWLEntity owlEntity) {
        InferredOntologyLoader inferredOntologyLoader = null;

        try {
            List<Vector<String>> list = new ArrayList<>();
            JenaOwlConverter converter = new JenaOwlConverter();
            inferredOntologyLoader = new InferredOntologyLoader();

            OWLOntology ont = inferredOntologyLoader.loadInferredOntology(revisionManager);
            Model dataModel = converter.convertOwlModelToJenaFormat(ont, FileUtils.langTurtle);

            // Load the main data model
            Model shapesModel = RdfModelReader.getModelFromString(editorText, FileUtils.langTurtle);
            final ShaclValidationReport report = ShaclValidation.runValidation(shapesModel, dataModel);
            List<ShaclValidationResultJena> validationResults = filterResults(report, owlEntity, inferredOntologyLoader.getReasoner());
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
        } finally {
            if (inferredOntologyLoader != null)
                inferredOntologyLoader.dispose();
        }
    }

    private List<ShaclValidationResultJena> filterResults(ShaclValidationReport report, OWLEntity owlEntity, OWLReasoner reasoner) {

        if (owlEntity == null) {
            return new ArrayList<>(report.validationResults);
        } else {
            Stream<ShaclValidationResultJena> results = report.validationResults.stream();

            if (owlEntity.isOWLNamedIndividual()) {
                OWLNamedIndividual selectedIndividual = owlEntity.asOWLNamedIndividual();
                String selectedIndividualIRI = selectedIndividual.getIRI().toString();

                results = results.filter(row -> row.focusNode != null && row.focusNode.isURIResource())
                        .filter(row -> row.focusNode.asResource().getURI().equals(selectedIndividualIRI));
            } else if (owlEntity.isOWLClass()) {
                OWLClass selectedClass = owlEntity.asOWLClass();

                // don't filter if owl:Thing is selected
                if (!selectedClass.isTopEntity()) {
                    Set<String> instanceIRIs = getInstanceIRIs(selectedClass, reasoner);

                    results = results.filter(row -> row.focusNode != null && row.focusNode.isURIResource())
                            .filter(row -> instanceIRIs.contains(row.focusNode.asResource().getURI()));
                }
            } else {
                // TODO: filter on row.resultPath for object / data properties
                // NOTE: (currently) not needed, as those can not be selected in the current tab layout
            }

            return results.collect(Collectors.toList());
        }
    }

    private Set<String> getInstanceIRIs(OWLClass selectedClass, OWLReasoner reasoner) {

        if (reasoner != null /* && (reasonerManager.getReasonerStatus() == ReasonerStatus.INITIALIZED
                || reasonerManager.getReasonerStatus() == ReasonerStatus.OUT_OF_SYNC)*/) {
            // direct = false -> retrieve all instances, not only direct instances
            NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(selectedClass, false);

            return instances.getFlattened().stream().map(i -> i.getIRI().toString()).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }


}
