package org.mybatis.pagination.helpers;

/**
 * <p>
 * Help class by String.
 * </p>
 *
 * @author mumu@yfyang
 * @version 1.0 2013-09-09 11:26 AM
 * @since JDK 1.5
 */
public final class StringHelper {
    public static final String EMPTY = "";

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * <p>Capitalizes a String changing the first letter to title case as
     * per {@link Character#toTitleCase(char)}. No other letters are changed.</p>
     * <p/>
     * A {@code null} input String returns {@code null}.</p>
     * <p/>
     * <pre>
     * StringUtils.capitalize(null)  = null
     * StringUtils.capitalize("")    = ""
     * StringUtils.capitalize("cat") = "Cat"
     * StringUtils.capitalize("cAt") = "CAt"
     * </pre>
     *
     * @param str the String to capitalize, may be null
     * @return the capitalized String, {@code null} if null String input
     */
    public static String capitalize(final String str) {
        if (isEmpty(str)) {
            return str;
        }

        char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }

        return String.valueOf(Character.toTitleCase(firstChar)) + str.substring(1);
    }
}
