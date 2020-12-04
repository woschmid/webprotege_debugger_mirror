package edu.stanford.bmir.protege.web.server.debugger;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;

public class OWLEntityExtractor implements OWLLogicalAxiomVisitorEx<OWLEntity>, OWLAxiomVisitorEx<OWLEntity> {

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLSubClassOfAxiom axiom) {
        return axiom.getSuperClass().asOWLClass();
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLAsymmetricObjectPropertyAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLDisjointClassesAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLDisjointUnionAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLSymmetricObjectPropertyAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLClassAssertionAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLTransitiveObjectPropertyAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLSameIndividualAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLSubPropertyChainOfAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLInverseObjectPropertiesAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLHasKeyAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull SWRLRule rule) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLDeclarationAxiom axiom) {
        return axiom.getEntity();
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLDatatypeDefinitionAxiom axiom) {
        return axiom.getDatatype();
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
        return axiom.getProperty();
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
        return null;
    }

    @Nonnull
    @Override
    public OWLEntity visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
        return null;
    }
}
