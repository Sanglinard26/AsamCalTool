/*
 * Creation : 21 févr. 2019
 */
package utils;

import java.util.regex.Pattern;

public final class RegexHolder {

    public static final Pattern LINE_COMMENT = Pattern.compile("/\\*.*?\\*/");
    public static final Pattern QUOTE = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
    // public static final Pattern DOUBLE_QUOTE = Pattern.compile("\".*\"");

    public static final boolean isString(String line) {
        // return DOUBLE_QUOTE.matcher(line).matches();
        return line.charAt(0) == '"' && line.charAt(line.length() - 1) == '"';
    }

}
