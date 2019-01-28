package velites.java.utility.misc;

import velites.java.utility.generic.Func0;

/**
 * Created by regis on 17/4/19.
 */

public class InfoDescriptor {
    private final Func0<String> descriptor;

    public InfoDescriptor(Func0<String> descriptor) {

        this.descriptor = descriptor;
    }

    public InfoDescriptor(final String description) {
        this(new Func0() {
            @Override
            public Object f() {
                return description;
            }
        });
    }

    @Override
    public String toString() {
        if (descriptor == null) {
            return super.toString();
        } else {
            return descriptor.f();
        }
    }
}
