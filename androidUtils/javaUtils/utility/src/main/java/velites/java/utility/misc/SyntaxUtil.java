package velites.java.utility.misc;

import velites.java.utility.generic.Func0;

/**
 * Created by regis on 17/4/19.
 */

public final class SyntaxUtil {
    private SyntaxUtil() {}

    public static final <T> T nvl(T value, Func0<T> obtainReplacement) {
        if (value != null) {
            return value;
        }
        if (obtainReplacement == null) {
            return null;
        }
        return obtainReplacement.f();
    }

    public static final <T> T nvl(T value, T replacement) {
        if (value != null) {
            return value;
        }
        return replacement;
    }

    public static final boolean nvl(Boolean value, boolean replacement) {
        return value == null ? replacement : value;
    }

    public static final boolean nvl(Boolean value) {
        return nvl(value, false);
    }

    public static final int nvl(Integer value, int replacement) {
        return value == null ? replacement : value;
    }

    public static final int nvl(Integer value) {
        return nvl(value, 0);
    }

    public static final long nvl(Long value, int replacement) {
        return value == null ? replacement : value;
    }

    public static final long nvl(Long value) {
        return nvl(value, 0);
    }
}
