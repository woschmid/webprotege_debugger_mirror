package edu.stanford.bmir.protege.web.client.debugger;

import java.util.ArrayList;
import java.util.List;

public class AutoConfigAxiom {
    private String axiom;
    // TODO: define which keyword should be added ":"
    String[] keywords = {"ValuePartition","Ontology", "Import", "Prefix", "Class","ObjectProperty","DataProperty", "Individual",
            "Datatype","AnnotationProperty", "SubClassOf", "SuperClassOf", "EquivalentTo","EquivalentClasses","EquivalentProperties", "DisjointWith",
            "Individuals","DisjointClasses", "DisjointProperties", "DisjointUnionOf", "Facts","SameAs","SameIndividual", "DifferentFrom",
            "DifferentIndividuals",">=", "<=", ">", "<",",","Types", "Type",
            "Annotations","Domain", "Range", "Characteristics", "InverseOf","Inverses","SubPropertyOf", "SuperPropertyOf",
            "SubPropertyChain","HasKey", "Rule"};

    public AutoConfigAxiom(String axiom){
        this.axiom = axiom;
    }

    public String fixAxiom(){
        String[] strArr = axiom.split(" ");
        for (String k :
                keywords) {
            if (strArr[1].equals(k)){
                strArr[1]+= ":";
            }
        }
        return String.join(" ", strArr);
    }
}
