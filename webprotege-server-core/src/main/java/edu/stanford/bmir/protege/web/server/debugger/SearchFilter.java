package edu.stanford.bmir.protege.web.server.debugger;

public class SearchFilter {

    private boolean aBox = false;

    private boolean tBox = false;

    private boolean rBox = false;

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
        aBox = false;
        tBox = false;
        rBox = false;
    }
}
