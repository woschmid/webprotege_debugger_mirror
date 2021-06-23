package edu.stanford.bmir.protege.web.server.shacl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShaclValidationReport {

    public final boolean conforms;
    public final Set<ShaclValidationResultJena> validationResults;

    public ShaclValidationReport(Resource report) {
        Model model = report.getModel();

        Resource validationReport =
                model.listStatements(null, RDF.type, SH.ValidationReport).toList().get(0).getSubject();
        this.conforms = validationReport.getProperty(SH.conforms).getObject().asLiteral().getBoolean();

        List<Statement> validationResults = model.listStatements(null, RDF.type, SH.ValidationResult).toList();

        this.validationResults = new HashSet<>(validationResults.size());
        for (Statement stmt : validationResults) {
            Resource subject = stmt.getSubject();

            ShaclValidationResultJena validationResult = new ShaclValidationResultJena(model, subject);

            this.validationResults.add(validationResult);
        }
    }
}
