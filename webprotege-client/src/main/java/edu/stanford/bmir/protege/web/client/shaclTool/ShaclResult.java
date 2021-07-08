package edu.stanford.bmir.protege.web.client.shaclTool;

import edu.stanford.bmir.protege.web.client.shaclTool.shaclEditor.ShaclEditorPresenter;
import edu.stanford.bmir.protege.web.client.shaclTool.shaclResultTable.ShaclResultTablePresenter;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.bmir.protege.web.shared.shacl.ShaclValidationResult;

import javax.inject.Inject;
import javax.validation.constraints.Null;

@ProjectSingleton
public class ShaclResult {

    ShaclEditorPresenter shaclEditorPresenter;

    ShaclResultTablePresenter shaclResultTablePresenter;

    String shaclContent;
    @Inject
    public ShaclResult() {}

    ShaclValidationResult shaclValidationResult = new ShaclValidationResult(null);

    public ShaclValidationResult getShaclValidationResult() {
        return shaclValidationResult;
    }

    public void setShaclValidationResult(ShaclValidationResult shaclValidationResult) {
        this.shaclValidationResult = shaclValidationResult;
    }

    public void setShaclEditorPresenter(ShaclEditorPresenter shaclEditorPresenter) {
        this.shaclEditorPresenter = shaclEditorPresenter;
    }

    public void setShaclResultTablePresenter(ShaclResultTablePresenter shaclResultTablePresenter) {
        this.shaclResultTablePresenter = shaclResultTablePresenter;
    }

    public void notifyToUpdate(){
        shaclResultTablePresenter.setData(shaclValidationResult.getValidationResult());
    }

    public String getShaclContent() {
        return shaclContent;
    }

    public void setShaclContent(String shaclContent) {
        this.shaclContent = shaclContent;
    }
}
