package velites.java.utility.merge;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Set;

import de.danielbechler.diff.access.PropertyAwareAccessor;
import de.danielbechler.diff.selector.ElementSelector;
import velites.java.utility.misc.ExceptionUtil;

public class FieldAccessor implements PropertyAwareAccessor {
    private Class<?> type;
    private Field field;

    public FieldAccessor(Class<?> type, Field field) {
        this.type = type;
        this.field = field;
    }

    @Override
    public String getPropertyName() {
        return this.field.getName();
    }

    @Override
    public Set<Annotation> getFieldAnnotations() {
        return null;
    }

    @Override
    public <T extends Annotation> T getFieldAnnotation(Class<T> annotationClass) {
        return null;
    }

    @Override
    public Set<Annotation> getReadMethodAnnotations() {
        return null;
    }

    @Override
    public <T extends Annotation> T getReadMethodAnnotation(Class<T> annotationClass) {
        return null;
    }

    @Override
    public ElementSelector getElementSelector() {
        return new FieldElementSelector(this.getPropertyName());
    }

    @Override
    public Object get(final Object target) {
        if (target == null) {
            return null;
        }
        return ExceptionUtil.runAndRethrowAsRuntime(() -> field.get(target), null);
    }

    @Override
    public void set(Object target, Object value) {
        if (target != null) {
            ExceptionUtil.runAndRethrowAsRuntime(() -> field.set(target, value), null);
        }
    }

    @Override
    public void unset(Object target) {
        this.set(target, null);
    }

    @Override
    public Set<String> getCategoriesFromAnnotation() {
        return null;
    }

    @Override
    public boolean isExcludedByAnnotation() {
        return false;
    }

    @Override
    public Class<?> getType() {
        return this.type;
    }
}
