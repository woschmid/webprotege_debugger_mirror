package edu.stanford.bmir.protege.web.shared.debugger;

import edu.stanford.bmir.protege.web.shared.HasEntity;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nullable;

public class GetEntityResult implements Result, HasEntity {

    @Nullable
    private OWLEntity entity;

    @GwtSerializationConstructor
    private GetEntityResult() {}

    public GetEntityResult(@Nullable OWLEntity entity) {
        this.entity = entity;
    }

    @Override
    @Nullable
    public OWLEntity getEntity() {
        return entity;
    }

}
