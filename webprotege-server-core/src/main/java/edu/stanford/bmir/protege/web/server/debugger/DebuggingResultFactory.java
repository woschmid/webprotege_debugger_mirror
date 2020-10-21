package edu.stanford.bmir.protege.web.server.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.server.renderer.RenderingManager;
import edu.stanford.bmir.protege.web.shared.debugger.CorrectAxioms;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.PossiblyFaultyAxioms;
import edu.stanford.bmir.protege.web.shared.debugger.SessionState;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Query;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A factory for DebuggingSessionStateResult instances to be used as
 */
public class DebuggingResultFactory {

    @Nonnull
    protected static DebuggingSessionStateResult generateResult(@Nonnull DebuggingSession session, @Nonnull Boolean isOk, @Nullable String message) {

        List<edu.stanford.bmir.protege.web.shared.debugger.TestCase> p = new ArrayList<>();
        List<edu.stanford.bmir.protege.web.shared.debugger.TestCase> n = new ArrayList<>();
        PossiblyFaultyAxioms possiblyFaultyAxioms = null;
        CorrectAxioms correctAxioms = null;

        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = session.getDiagnosisModel();

        if (diagnosisModel != null) {
            for (OWLLogicalAxiom a : diagnosisModel.getEntailedExamples())
                p.add(new edu.stanford.bmir.protege.web.shared.debugger.TestCase(session.getRenderingManager().getHtmlBrowserText(a)));

            for (OWLLogicalAxiom a : diagnosisModel.getNotEntailedExamples())
                n.add(new edu.stanford.bmir.protege.web.shared.debugger.TestCase(session.getRenderingManager().getHtmlBrowserText(a)));

            Set<SafeHtml> possiblyFaultySet = new HashSet<>();
            for (OWLLogicalAxiom a: diagnosisModel.getPossiblyFaultyFormulas())
                possiblyFaultySet.add(session.getRenderingManager().getHtmlBrowserText(a));
            possiblyFaultyAxioms = new PossiblyFaultyAxioms(possiblyFaultySet);

            Set<SafeHtml> correctSet = new HashSet<>();
            for (OWLLogicalAxiom a: diagnosisModel.getCorrectFormulas())
                correctSet.add(session.getRenderingManager().getHtmlBrowserText(a));
            correctAxioms = new CorrectAxioms(correctSet);

        }

        final SessionState sessionState = session.getState();

        if (sessionState == SessionState.INIT)
            return new DebuggingSessionStateResult(Boolean.TRUE, session.getUserId(),null,null,p, n, possiblyFaultyAxioms, correctAxioms, sessionState, null);

        edu.stanford.bmir.protege.web.shared.debugger.Query q = null;
        List<edu.stanford.bmir.protege.web.shared.debugger.Diagnosis> d = new ArrayList<>();

        // get the state of the session
        final Query<OWLLogicalAxiom> query = session.getQuery();
        final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = session.getDiagnoses();

        if (query != null)
            q = new edu.stanford.bmir.protege.web.shared.debugger.Query(renderAxioms(query.formulas, session.getRenderingManager()));

        if (diagnoses != null)
            for (org.exquisite.core.model.Diagnosis<OWLLogicalAxiom> diag : diagnoses)
                d.add(new edu.stanford.bmir.protege.web.shared.debugger.Diagnosis(renderAxioms(diag.getFormulas(), session.getRenderingManager())));



        return new DebuggingSessionStateResult(isOk, session.getUserId(), q, d, p, n, possiblyFaultyAxioms, correctAxioms, sessionState, message);
    }

    @Nonnull
    private static Set<SafeHtml> renderAxioms(@Nonnull Set<OWLLogicalAxiom> axioms, @Nonnull RenderingManager renderingManager) {
        final Set<SafeHtml> renderedAxioms = new HashSet<>();
        for (OWLLogicalAxiom axiom : axioms)
            renderedAxioms.add(renderingManager.getHtmlBrowserText(axiom));
        return renderedAxioms;
    }

}
