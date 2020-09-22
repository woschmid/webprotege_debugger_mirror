package edu.stanford.bmir.protege.web.server.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.server.renderer.RenderingManager;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.SessionState;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Query;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DebuggingResultFactory {

    @Nonnull
    protected static DebuggingSessionStateResult getDebuggingSessionStateResult(@Nonnull DebuggingSession session) {

        if (session.getState() == SessionState.INIT)
            return new DebuggingSessionStateResult(Boolean.TRUE, session.getUserId(),null,null,null, null, null, null);

        edu.stanford.bmir.protege.web.shared.debugger.Query q = null;
        List<edu.stanford.bmir.protege.web.shared.debugger.Diagnosis> d = new ArrayList<>();
        List<edu.stanford.bmir.protege.web.shared.debugger.TestCase> p = new ArrayList<>();
        List<edu.stanford.bmir.protege.web.shared.debugger.TestCase> n = new ArrayList<>();

        // get the state of the session
        final Query<OWLLogicalAxiom> query = session.getQuery();
        final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = session.getDiagnoses();
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = session.getDiagnosisModel();
        final SessionState sessionState = session.getState();

        if (query != null)
            q = new edu.stanford.bmir.protege.web.shared.debugger.Query(renderAxioms(query.formulas, session.getRenderingManager()));

        if (diagnoses != null)
            for (org.exquisite.core.model.Diagnosis<OWLLogicalAxiom> diag : diagnoses)
                d.add(new edu.stanford.bmir.protege.web.shared.debugger.Diagnosis(renderAxioms(diag.getFormulas(), session.getRenderingManager())));

        if (diagnosisModel != null) {
            for (OWLLogicalAxiom a : diagnosisModel.getEntailedExamples())
                p.add(new edu.stanford.bmir.protege.web.shared.debugger.TestCase(session.getRenderingManager().getHtmlBrowserText(a)));

            for (OWLLogicalAxiom a : diagnosisModel.getNotEntailedExamples())
                n.add(new edu.stanford.bmir.protege.web.shared.debugger.TestCase(session.getRenderingManager().getHtmlBrowserText(a)));
        }

        return new DebuggingSessionStateResult(Boolean.TRUE, session.getUserId(), q, d, p, n, sessionState, null);
    }

    @Nonnull
    protected static DebuggingSessionStateResult getFailureDebuggingSessionStateResult(@Nonnull DebuggingSession session, @Nonnull String errorMessage) {

        edu.stanford.bmir.protege.web.shared.debugger.Query q = null;
        List<edu.stanford.bmir.protege.web.shared.debugger.Diagnosis> d = new ArrayList<>();
        List<edu.stanford.bmir.protege.web.shared.debugger.TestCase> p = new ArrayList<>();
        List<edu.stanford.bmir.protege.web.shared.debugger.TestCase> n = new ArrayList<>();

        // get the state of the session
        final Query<OWLLogicalAxiom> query = session.getQuery();
        final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = session.getDiagnoses();
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = session.getDiagnosisModel();
        final SessionState sessionState = session.getState();

        if (query != null)
            q = new edu.stanford.bmir.protege.web.shared.debugger.Query(renderAxioms(query.formulas, session.getRenderingManager()));

        if (diagnoses != null)
            for (org.exquisite.core.model.Diagnosis<OWLLogicalAxiom> diag : diagnoses)
                d.add(new edu.stanford.bmir.protege.web.shared.debugger.Diagnosis(renderAxioms(diag.getFormulas(), session.getRenderingManager())));

        if (diagnosisModel != null) {
            for (OWLLogicalAxiom a : diagnosisModel.getEntailedExamples())
                p.add(new edu.stanford.bmir.protege.web.shared.debugger.TestCase(session.getRenderingManager().getHtmlBrowserText(a)));

            for (OWLLogicalAxiom a : diagnosisModel.getNotEntailedExamples())
                n.add(new edu.stanford.bmir.protege.web.shared.debugger.TestCase(session.getRenderingManager().getHtmlBrowserText(a)));
        }

        return new DebuggingSessionStateResult(Boolean.FALSE, session.getUserId(), q, d, p, n, sessionState, errorMessage);
    }

    @Nonnull
    private static Set<SafeHtml> renderAxioms(@Nonnull Set<OWLLogicalAxiom> axioms, @Nonnull RenderingManager renderingManager) {
        final Set<SafeHtml> renderedAxioms = new HashSet<>();
        for (OWLLogicalAxiom axiom : axioms)
            renderedAxioms.add(renderingManager.getHtmlBrowserText(axiom));
        return renderedAxioms;
    }

}
