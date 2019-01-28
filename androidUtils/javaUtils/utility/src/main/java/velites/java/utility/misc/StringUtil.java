package velites.java.utility.misc;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Locale;

import velites.java.utility.generic.Action2;
import velites.java.utility.generic.Action3;

public final class StringUtil {
    private StringUtil() {}

    public static final String STRING_EMPTY = "";
    public static final String STRING_SPACE = " ";
    public static final String STRING_NEW_LINE = System.getProperty("line.separator");
    public static final Locale INVARIANT_LOCALE = Locale.US;

    public static String formatInvariant(String format, Object... args) {
        if (args == null || args.length < 1) {
            return format;
        }
        return String.format(INVARIANT_LOCALE, format, args);
    }

    public static String emptyIfNull(String str) {
        return str == null ? STRING_EMPTY : str;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() <= 0;
    }

    public static boolean isNullOrSpace(String str) {
        return str == null || str.trim().length() <= 0;
    }

    public static boolean isNullOrEmpty(CharSequence str) {
        return str == null || str.length() <= 0;
    }

    public static boolean isNullOrSpace(CharSequence str) {
        return str == null || str.toString().trim().length() <= 0;
    }

    public static void truncateToActions(String str, int limit, String[] delimiters, Action3<Integer, String, String> act) {
        if (str == null || limit <= 0 || act == null) {
            return;
        }
        int index = 0;
        String cur;
        int pos;
        String hit = null;
        while (str.length() > 0) {
            if (str.length() > limit) {
                pos = limit;
                if (delimiters != null) {
                    for (String d : delimiters) {
                        if (isNullOrEmpty(d)) {
                            continue;
                        }
                        pos = str.lastIndexOf(d, limit);
                        if (pos > 0) { // not truncate from head
                            hit = d;
                            break;
                        }
                    }
                }
                if (pos < 0) {
                    pos = limit;
                }
                cur = str.substring(0, pos);
                str = str.substring(pos + (hit == null ? 0 : hit.length()));
            } else {
                cur = str;
                str = STRING_EMPTY;
            }
            act.a(index++, hit, cur);
        }
    }

    public static String join(boolean skipEmpty, CharSequence sep, Object... values) {
        if (values == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object v : values) {
            String str = v == null ? null : v.toString();
            if (skipEmpty && StringUtil.isNullOrEmpty(str)) {
                continue;
            }
            if (!first) {
                sb.append(sep);
            }
            sb.append(str);
            first = false;
        }
        return sb.toString();
    }
    public static String join(boolean skipEmpty, CharSequence sep, Iterable values) {
        if (values == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object v : values) {
            String str = v == null ? null : v.toString();
            if (skipEmpty && StringUtil.isNullOrEmpty(str)) {
                continue;
            }
            if (!first) {
                sb.append(sep);
            }
            sb.append(str);
            first = false;
        }
        return sb.toString();
    }

    public static String padRight(String str, char c, int targetLength) {
        if (str != null) {
            while (str.length() < targetLength) {
                str += c;
            }
        }
        return str;
    }

    public static String padLeft(String str, char c, int targetLength) {
        if (str != null) {
            while (str.length() < targetLength) {
                str = c + str;
            }
        }
        return str;
    }

    public static String escapeQuote(String str) {
        if (str == null) {
            return null;
        }
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
