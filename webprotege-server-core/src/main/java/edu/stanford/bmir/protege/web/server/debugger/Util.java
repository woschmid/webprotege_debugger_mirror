package edu.stanford.bmir.protege.web.server.debugger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Util {

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

    /**
     * Logs the error with the exception stacktrace.
     * @param clazz the  logger will be named after clazz.
     * @param e The exception.
     */
    public static void logException(@Nonnull final Class<?> clazz, @Nonnull final Throwable e) {
        final Logger logger = LoggerFactory.getLogger(clazz);
        logger.error(e.getMessage(), e);
        for (StackTraceElement element : e.getStackTrace())
            logger.error(element.toString());

    }
}
