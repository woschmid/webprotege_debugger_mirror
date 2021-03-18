package edu.stanford.bmir.protege.web.server.debugger;

import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.server.renderer.RenderingManager;
import edu.stanford.bmir.protege.web.shared.debugger.CorrectAxioms;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingSessionStateResult;
import edu.stanford.bmir.protege.web.shared.debugger.PossiblyFaultyAxioms;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Query;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

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
            final Collection<OWLLogicalAxiom> presentedPossiblyFaultyAxioms = filterAndPaginatePossiblyFaultyAxioms(diagnosisModel.getPossiblyFaultyFormulas(), session);
            possiblyFaultyAxioms = new PossiblyFaultyAxioms(renderAxioms(presentedPossiblyFaultyAxioms, session.getRenderingManager()));
            session.setPresentedPossiblyFaultyAxioms(presentedPossiblyFaultyAxioms);
            final Collection<OWLLogicalAxiom> presentedCorrectAxioms = filterAndPaginateCorrectAxioms(diagnosisModel.getCorrectFormulas(), session);
            correctAxioms = new CorrectAxioms(renderAxioms(presentedCorrectAxioms, session.getRenderingManager()));
            session.setPresentedCorrectAxioms(presentedCorrectAxioms);
        }

        session.keepSessionAlive();

        return new DebuggingSessionStateResult(isOk,
                session.getUserId(),
                query,
                diagnoses,
                positiveTestCases,
                negativeTestCases,
                possiblyFaultyAxioms,
                correctAxioms,
                session.getState(),
                Util.escapeHtml(message),
                session.getSearchFilter().isABox(),
                session.getSearchFilter().isTBox(),
                session.getSearchFilter().isRBox(),
                session.getCurrentPossiblyFaultyPage(),
                session.getPossiblyFaultyPages(),
                session.getCurrentCorrectPage(),
                session.getCorrectPages(),
                session.getNrPossiblyFaultyAxioms(),
                session.getNrCorrectAxioms(),
                session.getPreferences()
        );
    }

    @Nonnull
    private static List<SafeHtml> renderAxioms(@Nonnull Collection<OWLLogicalAxiom> axioms, @Nonnull RenderingManager renderingManager) {
        final List<SafeHtml> renderedAxioms = new ArrayList<>();
        final Set<OWLLogicalAxiom> sortedAxioms = new TreeSet<>(axioms);

        for (OWLLogicalAxiom axiom : sortedAxioms)
            renderedAxioms.add(renderingManager.getHtmlBrowserText(axiom));

        return renderedAxioms;
    }

    private static Collection<OWLLogicalAxiom> filterAndPaginatePossiblyFaultyAxioms(@Nonnull Collection<OWLLogicalAxiom> axioms, @Nonnull DebuggingSession session) {

        final List<OWLLogicalAxiom> list = axioms.stream().filter(axiom -> doesSearchFilterMatch(axiom, session.getSearchFilter())).distinct().sorted().collect(Collectors.toCollection(ArrayList<OWLLogicalAxiom>::new));
        int size = list.size();

        session.setNrPossiblyFaultyAxioms(size);

        if (size > 0) {
            // check the maximal possible page
            int maxPage = (size / session.getPreferences().getMaxVisiblePossiblyFaultyAxioms());
            if ((size % session.getPreferences().getMaxVisiblePossiblyFaultyAxioms()) > 0) maxPage += 1;
            if (session.getCurrentPossiblyFaultyPage() > maxPage)
                session.setCurrentPossiblyFaultyPage(maxPage);
            if (session.getCurrentPossiblyFaultyPage() <= 0)
                session.setCurrentPossiblyFaultyPage(1);

            // set the number of possible pages
            session.setPossiblyFaultyPages(maxPage);

            int fromIndex = session.getPreferences().getMaxVisiblePossiblyFaultyAxioms() * (session.getCurrentPossiblyFaultyPage() - 1); // inclusive
            int toIndex = fromIndex + session.getPreferences().getMaxVisiblePossiblyFaultyAxioms(); // exclusive
            if (toIndex > size)
                toIndex = size;

            return list.subList(fromIndex, toIndex);
        } else {
            session.setCurrentPossiblyFaultyPage(0);
            session.setPossiblyFaultyPages(0);
            return Collections.emptyList();
        }
    }

    private static Collection<OWLLogicalAxiom> filterAndPaginateCorrectAxioms(@Nonnull Collection<OWLLogicalAxiom> axioms, @Nonnull DebuggingSession session) {

        final List<OWLLogicalAxiom> list = axioms.stream().filter(axiom -> doesSearchFilterMatch(axiom, session.getSearchFilter())).distinct().sorted().collect(Collectors.toCollection(ArrayList<OWLLogicalAxiom>::new));
        int size = list.size();

        session.setNrCorrectAxioms(size);

        if (size > 0) {
            int maxPage = (size / session.getPreferences().getMaxVisibleCorrectAxioms());
            if ((size % session.getPreferences().getMaxVisibleCorrectAxioms()) > 0) maxPage += 1;
            if (session.getCurrentCorrectPage() > maxPage)
                session.setCurrentCorrectPage(maxPage);
            if (session.getCurrentCorrectPage() <= 0)
                session.setCurrentCorrectPage(1);

            session.setCorrectPages(maxPage);

            int fromIndex = session.getPreferences().getMaxVisibleCorrectAxioms() * (session.getCurrentCorrectPage() - 1); // inclusive
            int toIndex = fromIndex + session.getPreferences().getMaxVisibleCorrectAxioms(); // exclusive
            if (toIndex > size)
                toIndex = size;

            return list.subList(fromIndex, toIndex);
        } else {
            session.setCurrentCorrectPage(0);
            session.setCorrectPages(0);
            return Collections.emptyList();
        }
    }

    private static boolean doesSearchFilterMatch(@Nonnull OWLLogicalAxiom axiom, @Nonnull SearchFilter filter) {
        final AxiomType<?> axiomType = axiom.getAxiomType();
        return (
                (filter.isTBox() && AxiomType.TBoxAxiomTypes.contains(axiomType))
                ||
                (filter.isABox() && AxiomType.ABoxAxiomTypes.contains(axiomType))
                ||
                (filter.isRBox() && AxiomType.RBoxAxiomTypes.contains(axiomType))
                );
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
