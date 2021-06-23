package edu.stanford.bmir.protege.web.server.shacl;

import org.apache.jena.rdf.model.RDFNode;
import org.topbraid.shacl.vocabulary.SH;

import java.util.Comparator;

public class ShaclValidationResultComparator implements Comparator<ShaclValidationResultJena> {

    public final static ShaclValidationResultComparator INSTANCE = new ShaclValidationResultComparator();

    private static int compareSeverity(RDFNode n1, RDFNode n2) {
        int s1 = getSeverityNumber(n1);
        int s2 = getSeverityNumber(n2);

        // Violation first, then Warning, then Info, then alphabetical from A to Z for custom severities
        if (s1 == 0 && s2 == 0) {
            return JenaOwlConverter.compareRDFNode(n1, n2);
        } else {
            return Integer.compare(s1, s2);
        }
    }

    private static int getSeverityNumber(RDFNode n) {
        if (n != null) {
            if (n.equals(SH.Violation)) {
                return 1;
            } else if (n.equals(SH.Warning)) {
                return 2;
            } else if (n.equals(SH.Info)) {
                return 3;
            }
        }

        return 0;
    }

    @Override public int compare(ShaclValidationResultJena r1, ShaclValidationResultJena r2) {
        int compareSeverity = compareSeverity(r1.resultSeverity, r2.resultSeverity);
        if (compareSeverity != 0)
            return compareSeverity;

        int compareFocusNode = JenaOwlConverter.compareRDFNode(r1.focusNode, r2.focusNode);
        if (compareFocusNode != 0)
            return compareFocusNode;

        int compareResultPath = JenaOwlConverter.compareRDFNode(r1.resultPath, r2.resultPath);
        if (compareResultPath != 0)
            return compareResultPath;

        int compareShape = JenaOwlConverter.compareRDFNode(r1.sourceShape, r2.sourceShape);
        if (compareShape != 0)
            return compareShape;

        int compareResultMessage = JenaOwlConverter.compareRDFNode(r1.resultMessage, r2.resultMessage);
        if (compareResultMessage != 0)
            return compareResultMessage;

        return JenaOwlConverter.compareRDFNode(r1.value, r2.value);
    }
}
