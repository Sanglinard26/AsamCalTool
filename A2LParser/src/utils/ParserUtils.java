/*
 * Creation : 21 févr. 2019
 */
package utils;

import java.util.regex.Pattern;

public final class ParserUtils {

    public static final Pattern LINE_COMMENT = Pattern.compile("/\\*.*?\\*/");
    public static final Pattern QUOTE = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");

    public static final boolean isUniqueString(String line) {

        final int nbChar = line.length();
        byte counter = 2;

        for (int i = 1; i < nbChar - 1; i++) {
            if (line.charAt(i) == '"') {
                if (line.charAt(i - 1) != '\\' && line.charAt(i + 1) != '"') {
                    counter++;
                } else {
                    i++;
                }
            }
        }

        return counter == 2;
    }

    public static String replaceSlashDoubleQuote(String s) {
        if ((s.indexOf('\\') == -1) && (s.indexOf('"') == -1))
            return s;
        StringBuilder sb = new StringBuilder();
        char oldC = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || (c == '\"' && oldC == '\\')) {
                oldC = c;
                continue;
            }
            oldC = c;
            sb.append(c);
        }
        return sb.toString();
    }

    public static final byte countQuote(String line) {
        final int nbChar = line.length();
        byte nbQuote = 0;

        for (int i = 0; i < nbChar; i++) {
            if (line.charAt(i) == '"') {
                nbQuote++;
            }
        }

        return nbQuote;
    }

    public static final boolean isEvenQuote(String line) {
        final byte nbQuote = countQuote(line);
        return (nbQuote & 1) != 1;
    }

}
