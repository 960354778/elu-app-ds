package velites.java.utility.misc;

import java.util.List;
import java.util.Map;

public class CollectionUtil {
    private CollectionUtil() {}

    public static <T> boolean isNullOrEmpty(T[] array) {
        return array == null || array.length < 1;
    }

    public static <T> boolean isNullOrEmpty(List<T> list) {
        return list == null || list.size() < 1;
    }
    public static <T, V> boolean isNullOrEmpty(Map<T, V> map) {
        return map == null || map.size() < 1;
    }
}
