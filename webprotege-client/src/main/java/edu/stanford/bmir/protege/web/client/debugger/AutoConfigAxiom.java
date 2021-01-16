package edu.stanford.bmir.protege.web.client.debugger;

public class AutoConfigAxiom {
    private String axiom;

    public AutoConfigAxiom(String axiom){
        this.axiom = axiom;
    }

    public String fixAxiom(){
        String[] strArr = axiom.split(" ");
//        if strArr[1].equals("")
        return axiom;
    }
}
