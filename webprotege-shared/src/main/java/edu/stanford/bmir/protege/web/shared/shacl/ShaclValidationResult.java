package edu.stanford.bmir.protege.web.shared.shacl;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;

import java.util.List;
import java.util.Vector;

public class ShaclValidationResult implements Result {

    private List<Vector<String>> validationResult;

    @GwtSerializationConstructor
    private ShaclValidationResult() {
    }

    public ShaclValidationResult(List<Vector<String>> validationResult) {
        this.validationResult = validationResult;
    }

    public List<Vector<String>> getValidationResult() {
        return validationResult;
    }
}
