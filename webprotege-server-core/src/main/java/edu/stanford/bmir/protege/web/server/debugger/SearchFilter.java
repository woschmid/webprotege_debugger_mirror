package edu.stanford.bmir.protege.web.server.debugger;

import javax.annotation.Nullable;

/**
 * A Search Filter for presentation of A-Box, T-Box and/or R-Box axioms.
 */
public class SearchFilter {

    private boolean aBox = true;

    private boolean tBox = true;

    private boolean rBox = true;

    @Nullable
    private String searchString = null;

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

    @Nullable
    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(@Nullable String searchString) {
        if (searchString == null || searchString.trim().isEmpty())
            this.searchString = null;
        else
            this.searchString = searchString.trim();
    }

    public void reset() {
        aBox = true;
        tBox = true;
        rBox = true;
        searchString = null;
    }
}
