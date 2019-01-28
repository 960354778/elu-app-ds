package velites.java.utility.merge;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import de.danielbechler.diff.instantiation.TypeInfo;
import de.danielbechler.diff.introspection.Introspector;

public class IntrospectorByFields implements Introspector {
    @Override
    public TypeInfo introspect(Class<?> type) {
        TypeInfo ret = new TypeInfo(type);
        for (Field f : type.getFields()) {
            int m = f.getModifiers();
            if (Modifier.isPublic(m) && !Modifier.isFinal(m) && !Modifier.isStatic(m)) {
                ret.addPropertyAccessor(new FieldAccessor(type, f));
            }
        }
        return ret;
    }
}
