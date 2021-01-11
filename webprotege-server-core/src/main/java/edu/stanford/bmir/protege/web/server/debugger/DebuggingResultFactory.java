package edu.stanford.bmir.protege.web.server.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.server.renderer.RenderingManager;
import edu.stanford.bmir.protege.web.shared.debugger.CorrectAxioms;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.PossiblyFaultyAxioms;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Query;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * A factory for DebuggingSessionStateResult instances for returning results after actions.
 */
public class DebuggingResultFactory {

    /**
     * Generates a DebuggingSessionStateResult representing the result of an action and the current state of the backend.
     *
     * @param session The current DebuggingSession.
     * @param isOk <code>true</code> if the action was successful, <code>false</code> otherwise.
     * @param message An optional message for the frontend, <code>null</code> otherwise.
     * @return A DebuggingSessionStateResult representing the result of an action and the current state of the backend.
     */
    @Nonnull
    protected static DebuggingSessionStateResult generateResult(@Nonnull DebuggingSession session, @Nonnull Boolean isOk, @Nullable String message) {

        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = session.getDiagnosisModel();

        edu.stanford.bmir.protege.web.shared.debugger.Query query = renderQuery(session.getQuery(), session.getRenderingManager());
        List<edu.stanford.bmir.protege.web.shared.debugger.Diagnosis> diagnoses = renderDiagnoses(session.getDiagnoses(), session.getRenderingManager());
        List<edu.stanford.bmir.protege.web.shared.debugger.TestCase> positiveTestCases = new ArrayList<>();
        List<edu.stanford.bmir.protege.web.shared.debugger.TestCase> negativeTestCases = new ArrayList<>();
        edu.stanford.bmir.protege.web.shared.debugger.PossiblyFaultyAxioms possiblyFaultyAxioms = null;
        CorrectAxioms correctAxioms = null;

        if (diagnosisModel != null) {
            positiveTestCases = renderTestCases(diagnosisModel.getEntailedExamples(), session.getRenderingManager());
            negativeTestCases = renderTestCases(diagnosisModel.getNotEntailedExamples(), session.getRenderingManager());
            possiblyFaultyAxioms = new PossiblyFaultyAxioms(renderAxioms(diagnosisModel.getPossiblyFaultyFormulas(), session.getRenderingManager()));
            correctAxioms = new CorrectAxioms(renderAxioms(diagnosisModel.getCorrectFormulas(), session.getRenderingManager()));
        }

        return new DebuggingSessionStateResult(isOk, session.getUserId(),query,diagnoses,positiveTestCases, negativeTestCases, possiblyFaultyAxioms, correctAxioms, session.getState(), StringUtil.escapeHtml(message));
    }

    @Nonnull
    private static List<SafeHtml> renderAxioms(@Nonnull Collection<OWLLogicalAxiom> axioms, @Nonnull RenderingManager renderingManager) {
        final List<SafeHtml> renderedAxioms = new ArrayList<>();
        final Set<OWLLogicalAxiom> sortedAxioms = new TreeSet<>(axioms);

        for (OWLLogicalAxiom axiom : sortedAxioms)
            renderedAxioms.add(renderingManager.getHtmlBrowserText(axiom));
        return renderedAxioms;
    }

    @Nullable
    private static edu.stanford.bmir.protege.web.shared.debugger.Query renderQuery(@Nullable final Query<OWLLogicalAxiom> query, @Nonnull RenderingManager renderingManager) {
        if (query != null)
            return new edu.stanford.bmir.protege.web.shared.debugger.Query(renderAxioms(query.formulas, renderingManager));
        return null;
    }

    @Nonnull
    private static List<edu.stanford.bmir.protege.web.shared.debugger.Diagnosis> renderDiagnoses(@Nullable Set<Diagnosis<OWLLogicalAxiom>> diagnoses, @Nonnull RenderingManager renderingManager) {
        List<edu.stanford.bmir.protege.web.shared.debugger.Diagnosis> d = new ArrayList<>();
        if (diagnoses != null) {
            for (org.exquisite.core.model.Diagnosis<OWLLogicalAxiom> di : diagnoses)
                d.add(new edu.stanford.bmir.protege.web.shared.debugger.Diagnosis(renderAxioms(di.getFormulas(), renderingManager)));
        }
        return d;
    }

    @Nonnull
    private static List<edu.stanford.bmir.protege.web.shared.debugger.TestCase> renderTestCases(@Nonnull List<OWLLogicalAxiom> axioms, @Nonnull RenderingManager renderingManager) {
        List<edu.stanford.bmir.protege.web.shared.debugger.TestCase> list = new ArrayList<>();
        for (OWLLogicalAxiom a : axioms)
            list.add(new edu.stanford.bmir.protege.web.shared.debugger.TestCase(renderingManager.getHtmlBrowserText(a)));
        return list;
    }

}
