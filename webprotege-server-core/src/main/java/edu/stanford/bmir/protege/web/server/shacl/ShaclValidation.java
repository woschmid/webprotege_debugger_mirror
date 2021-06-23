package edu.stanford.bmir.protege.web.server.shacl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;

public class ShaclValidation {

    public static ShaclValidationReport runValidation(Model shaclModel, Model dataModel) {
        // Run the validator
        Resource results = ValidationUtil.validateModel(dataModel, shaclModel, true);

        // Print violations
        System.out.println("--- ************* ---");
        System.out.println(ModelPrinter.get().print(results.getModel()));

        return new ShaclValidationReport(results);

    }

}
