package velites.java.utility.dispose;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;

public final class AutoDisposeHub {
    private static HashMap<Object, AutoDisposeHub> instances = new HashMap<Object, AutoDisposeHub>();

    public static final AutoDisposeHub getInstance(Object target) {
        ExceptionUtil.assertArgumentNotNull(target, "target");

        synchronized (AutoDisposeHub.instances) {
            AutoDisposeHub ret = AutoDisposeHub.instances.get(target);
            if (ret == null) {
                ret = new AutoDisposeHub(target);
                AutoDisposeHub.instances.put(target, ret);
            }

            return ret;
        }
    }

    public static int disposeAll(AutoDisposeHost host) {
        int ret = 0;
        synchronized (instances) {
            for (AutoDisposeHub hub : instances.values()) {
                ret += hub.dispose(host);
            }
        }
        return ret;
    }

    private List<WeakReference<AutoDisposer>> disposables;
    private Object target;

    private AutoDisposeHub(Object target) {
        this.target = target;
        disposables = new ArrayList<WeakReference<AutoDisposer>>();
    }

    public void registerDisposable(AutoDisposer disposable) {
        if (disposable != null) {
            synchronized (disposables) {
                disposables.add(new WeakReference<AutoDisposer>(disposable));
            }
        }
    }

    private int dispose(AutoDisposeHost host) {
        return clearAndDispose(true, host);
    }

    private final int clearAndDispose(boolean needDispose, AutoDisposeHost host) {
        int ret = 0;
        synchronized (disposables) {
            int originSize = disposables.size();
            int clearedNum = 0;
            for (int i = 0; i < disposables.size();) {
                AutoDisposer d = disposables.get(i).get();
                if (d == null) {
                    disposables.remove(i);
                    clearedNum++;
                } else {
                    if (needDispose) {
                        ret += d.dispose(host);
                    }
                    i++;
                }
            }
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_VERBOSE, this,
                    "Cleared %d disposables out of %d (owner=\"%s\").", clearedNum, originSize, target));
        }

        return ret;
    }
}
