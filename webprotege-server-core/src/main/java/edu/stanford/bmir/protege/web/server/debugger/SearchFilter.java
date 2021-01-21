package edu.stanford.bmir.protege.web.server.debugger;

/**
 * A Search Filter for presentation of A-Box, T-Box and/or R-Box axioms.
 */
public class SearchFilter {

    private boolean aBox = true;

    private boolean tBox = true;

    private boolean rBox = true;

    public boolean isABox() {
        return aBox;
    }

    public void setABox(boolean aBox) {
        this.aBox = aBox;
    }

    public boolean isTBox() {
        return tBox;
    }

    public void setTBox(boolean tBox) {
        this.tBox = tBox;
    }

    public boolean isRBox() {
        return rBox;
    }

    public void setRBox(boolean rBox) {
        this.rBox = rBox;
    }

    public void reset() {
        aBox = true;
        tBox = true;
        rBox = true;
    }
}
