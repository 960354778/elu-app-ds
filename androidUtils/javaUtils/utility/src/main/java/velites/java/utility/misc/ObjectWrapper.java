package velites.java.utility.misc;

import velites.java.utility.misc.ObjectAccessor;

/**
 * Created by regis on 2018/6/7.
 */

public final class ObjectWrapper<TObj> implements ObjectAccessor<TObj> {

    public ObjectWrapper() {
    }

    public ObjectWrapper(TObj obj) {
        this.obj = obj;
    }

    private TObj obj;

    public TObj get() {
        return obj;
    }

    public void set(TObj obj) {
        this.obj = obj;
    }
}
