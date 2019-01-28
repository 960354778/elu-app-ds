package velites.java.utility.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import velites.java.utility.generic.Func1;

public final class SerializationUtil {
    private SerializationUtil() {}

    private static Gson defaultGson;

    public static void setDefaultGson(Gson gson) {
        defaultGson = gson;
    }

    public static Gson getDefaultGson() {
        Gson gson = defaultGson;
        if (gson == null) {
            gson = new GsonBuilder().create();
        }
        return gson;
    }

    public static String describe(Object obj) {
        return getDefaultGson().toJson(obj);
    }

    public static <TSource, TTarget> List<TTarget> convert(List<TSource> source, Func1<TSource, TTarget> conv) {
        if (source == null) {
            return null;
        }
        List<TTarget> ret = new ArrayList<>();
        for (TSource src : source) {
            ret.add(conv == null ? (TTarget) src : conv.f(src));
        }
        return ret;
    }

    public static <TSource, TTarget> List<TTarget> convert(TSource[] source, Func1<TSource, TTarget> conv) {
        if (source == null) {
            return null;
        }
        List<TTarget> ret = new ArrayList<>();
        for (TSource src : source) {
            ret.add(conv == null ? (TTarget) src : conv.f(src));
        }
        return ret;
    }
}
