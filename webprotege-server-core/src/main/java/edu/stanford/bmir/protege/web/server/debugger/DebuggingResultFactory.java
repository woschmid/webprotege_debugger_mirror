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
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DebuggingResultFactory {

    @Nonnull
    protected static DebuggingSessionStateResult getDebuggingSessionStateResult(@Nullable DebuggingSession debuggingSession, @Nonnull RenderingManager renderingManager) {

        if (debuggingSession == null)
            return new DebuggingSessionStateResult(null,null,null,null, null, null);

        edu.stanford.bmir.protege.web.shared.debugger.Query q = null;
        List<edu.stanford.bmir.protege.web.shared.debugger.Diagnosis> d = new ArrayList<>();
        List<edu.stanford.bmir.protege.web.shared.debugger.TestCase> p = new ArrayList<>();
        List<edu.stanford.bmir.protege.web.shared.debugger.TestCase> n = new ArrayList<>();

        // get the state of the debuggingSession
        final Query<OWLLogicalAxiom> query = debuggingSession.getQuery();
        final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = debuggingSession.getDiagnoses();
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = debuggingSession.getDiagnosisModel();
        final SessionState sessionState = debuggingSession.getState();

        if (query != null)
            q = new edu.stanford.bmir.protege.web.shared.debugger.Query(renderAxioms(query.formulas, renderingManager));

        if (diagnoses != null)
            for (org.exquisite.core.model.Diagnosis<OWLLogicalAxiom> diag : diagnoses)
                d.add(new edu.stanford.bmir.protege.web.shared.debugger.Diagnosis(renderAxioms(diag.getFormulas(), renderingManager)));

        if (diagnosisModel != null) {
            for (OWLLogicalAxiom a : diagnosisModel.getEntailedExamples())
                p.add(new edu.stanford.bmir.protege.web.shared.debugger.TestCase(renderingManager.getHtmlBrowserText(a)));

            for (OWLLogicalAxiom a : diagnosisModel.getNotEntailedExamples())
                n.add(new edu.stanford.bmir.protege.web.shared.debugger.TestCase(renderingManager.getHtmlBrowserText(a)));
        }

        return new DebuggingSessionStateResult(debuggingSession.getUserId(), q, d, p, n, sessionState);
    }

    @Nonnull
    private static Set<SafeHtml> renderAxioms(@Nonnull Set<OWLLogicalAxiom> axioms, @Nonnull RenderingManager renderingManager) {
        final Set<SafeHtml> renderedAxioms = new HashSet<>();
        for (OWLLogicalAxiom axiom : axioms)
            renderedAxioms.add(renderingManager.getHtmlBrowserText(axiom));
        return renderedAxioms;
    }

}
