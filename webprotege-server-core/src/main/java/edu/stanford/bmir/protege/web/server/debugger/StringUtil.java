package edu.stanford.bmir.protege.web.server.debugger;

import javax.annotation.Nullable;

public class StringUtil {

    /**
     * Escapes the characters in a String using HTML entities.
     * E.g. \n becomes &lt;br/&gt;
     * @param str a string possibly containing characters that have to be escaped for HTML.
     * @return an HTML compabible string.
     */
    @Nullable public static String escapeHtml(@Nullable String str) {
        if (str == null) return null;
        final String s = str.replaceAll("\\r?\\n", "<br/>");
        return s.replaceAll("\\t", "&nbsp; &nbsp; &nbsp;");

    }
}
