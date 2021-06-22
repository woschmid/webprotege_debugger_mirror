package edu.stanford.bmir.protege.web.server.shacl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.topbraid.shacl.vocabulary.SH;

public class ShaclValidationResultJena {

    public final Model model;

    public final RDFNode resultSeverity;
    public final RDFNode sourceShape;
    public final RDFNode resultMessage;
    public final RDFNode focusNode;
    public final RDFNode resultPath;
    public final RDFNode value;

    public ShaclValidationResultJena(Model model, Resource subject) {
        this.model = model;

        this.resultSeverity = tryGetObject(subject.getProperty(SH.resultSeverity));
        this.sourceShape = tryGetObject(subject.getProperty(SH.sourceShape));
        this.resultMessage = tryGetObject(subject.getProperty(SH.resultMessage));
        this.focusNode = tryGetObject(subject.getProperty(SH.focusNode));
        this.resultPath = tryGetObject(subject.getProperty(SH.resultPath));
        this.value = tryGetObject(subject.getProperty(SH.value));
    }

    private static RDFNode tryGetObject(Statement stmt) {
        if (stmt != null)
            return stmt.getObject();
        return null;
    }

}
