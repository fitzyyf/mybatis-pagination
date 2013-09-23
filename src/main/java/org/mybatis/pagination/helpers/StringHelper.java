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


    /** The empty String {@code ""}. */
    public static final String EMPTY = "";
    /** The dot String {@code ","}. */
    public static final String DOT_CHAR = ",";
    /** The blank String {@code " "}. */
    public static final String BLANK_CHAR = " ";
    /** The equal sign String {@code "="} */
    public static final String EQUAL_SIGN_CHAR = "=";
    /**
     * The like String {@code "like"}
     */
    public static final String LIKE_CHAR = " like ";
    private static final String INJECTION_SQL = ".*([';]+|(--)+).*";
    private static String LIKE_FORMATE = "'%%s%'";

    /**
     * <p>Checks if a CharSequence is not empty ("") and not null.</p>
     * <p/>
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is not empty and not null
     * @since 3.0 Changed signature from isNotEmpty(String) to isNotEmpty(CharSequence)
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * <p>Checks if a CharSequence is empty ("") or null.</p>
     * <p/>
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     * <p/>
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the CharSequence.
     * That functionality is available in isBlank().</p>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     * @since 3.0 Changed signature from isEmpty(String) to isEmpty(CharSequence)
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * <p>Capitalizes a String changing the first letter to title case as
     * per {@link Character#toTitleCase(char)}. No other letters are changed.</p>
     * <p/>
     * <p>For a word based algorithm, see {@link org.apache.commons.lang3.text.WordUtils#capitalize(String)}.
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
    public static String capitalize(String str) {
        if (str == null || (str.length()) == 0) {
            return str;
        }
        return String.valueOf(Character.toTitleCase(str.charAt(0))) + str.substring(1);
    }

    /**
     * <p>Checks if a CharSequence is whitespace, empty ("") or null.</p>
     * <p/>
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Transact sQL injection.
     *
     * @param sql the sql
     * @return the string
     */
    public static String transactSQLInjection(String sql) {
        return sql.replaceAll(INJECTION_SQL, " ");
    }

    /**
     * Like value.
     *
     * @param value the value
     * @return the string
     */
    public static String likeValue(String value) {
        return String.format(LIKE_FORMATE, transactSQLInjection(value));
    }

}
