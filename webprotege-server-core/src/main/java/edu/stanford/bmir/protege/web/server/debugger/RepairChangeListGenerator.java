package edu.stanford.bmir.protege.web.server.debugger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.server.change.ChangeApplicationResult;
import edu.stanford.bmir.protege.web.server.change.ChangeGenerationContext;
import edu.stanford.bmir.protege.web.server.change.ChangeListGenerator;
import edu.stanford.bmir.protege.web.server.change.OntologyChangeList;
import edu.stanford.bmir.protege.web.server.owlapi.RenameMap;
import org.exquisite.core.model.Diagnosis;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RepairChangeListGenerator implements ChangeListGenerator<RepairChangeResult> {

    private final DebuggingSession session;
    private final Diagnosis<OWLLogicalAxiom> diagnosis;
    private final ImmutableMap<SafeHtml, String> axiomsToModify;
    private final ImmutableSet<SafeHtml> axiomsToDelete;

    public RepairChangeListGenerator(DebuggingSession session, Diagnosis<OWLLogicalAxiom> diagnosis, ImmutableMap<SafeHtml, String> axiomsToModify, ImmutableSet<SafeHtml> axiomsToDelete) {
        this.session = session;
        this.diagnosis = diagnosis;
        this.axiomsToModify = axiomsToModify;
        this.axiomsToDelete = axiomsToDelete;
    }

    @Override
    public OntologyChangeList<RepairChangeResult> generateChanges(ChangeGenerationContext context)  {
        RepairChangeResult result = new RepairChangeResult(null);
        final OntologyChangeList.Builder<RepairChangeResult> changeList = new OntologyChangeList.Builder<>();
        try {
            deleteRepairAxioms(changeList, axiomsToDelete);
            modifyRepairAxioms(changeList, axiomsToModify);
        } catch (AxiomNotFoundException | OWLParserException ex) {
            result = new RepairChangeResult(ex);
        }
        return changeList.build(result);
    }

    @Override
    public RepairChangeResult getRenamedResult(RepairChangeResult result, RenameMap renameMap) {
        return result;
    }

    @Nonnull
    @Override
    public String getMessage(ChangeApplicationResult<RepairChangeResult> result) {
        return "Repair action of " + session;
    }

    /**
     * Helper method to prepare the changelist with RemoveAxiomChanges for repair axioms to remove.
     *
     * @param changeList The changelist.
     * @param safeHtmls A list of SafeHtml representations from repair axioms to remove.
     */
    private void deleteRepairAxioms(@Nonnull final OntologyChangeList.Builder<RepairChangeResult> changeList,
                                    @Nonnull final Set<SafeHtml> safeHtmls) throws AxiomNotFoundException {
        final Set<OWLLogicalAxiom> axiomsToDelete = new HashSet<>();
        final Set<OWLLogicalAxiom> diagnosisAxioms = diagnosis.getFormulas();
        for (SafeHtml safeHtml : safeHtmls) {
            final OWLLogicalAxiom axiom = session.lookupAxiomInCollection(safeHtml, diagnosisAxioms);
            axiomsToDelete.add(axiom);
        }
        axiomsToDelete.forEach(axiom -> changeList.removeAxiom(session.getOntologyID(), axiom));
    }

    /**
     * Helper method to prepare the changelist with RemoveAxiomChanges and AddAxiomChanges for
     * all repair axioms that needs to be modified.
     *
     * @param changeList The changelist.
     * @param axiomsToModify A map of safeHtml representations of axioms and their modified version.
     */
    private void modifyRepairAxioms(@Nonnull final OntologyChangeList.Builder<RepairChangeResult> changeList,
                                    @Nonnull final Map<SafeHtml, String> axiomsToModify) throws AxiomNotFoundException, OWLParserException {

        for (Map.Entry<SafeHtml, String> entry : axiomsToModify.entrySet()) {
            final OWLLogicalAxiom axiomToRemove = session.lookupAxiomInCollection(entry.getKey(), diagnosis.getFormulas());
            final OWLLogicalAxiom modifiedAxiom = OWLLogicalAxiomSyntaxParser.parse(session.getOntology(), entry.getValue());
            changeList.removeAxiom(session.getOntologyID(), axiomToRemove);
            changeList.addAxiom(session.getOntologyID(), modifiedAxiom);
        }

    }

}
