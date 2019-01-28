package velites.java.utility.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by regis on 4/7/2017.
 */

public class ScopingUtil {
    private static final ThreadLocal<List<String>> local = new ThreadLocal<>();

    private static List<String> obtainCurrentScopes() {
        List<String> scopes = local.get();
        if (scopes == null) {
            scopes = new ArrayList<>();
            local.set(scopes);
        }
        return scopes;
    }

    public static void RunInScope(Runnable r, String... names) {
        if (r == null) {
            return;
        }
        List<String> scopes = obtainCurrentScopes();
        Map<String, Boolean> alreadyIns = new HashMap<>();
        for (String name : names) {
            if (name != null) {
                boolean alreadyIn = scopes.contains(name);
                if (!alreadyIn) {
                    scopes.add(name);
                }
                alreadyIns.put(name, alreadyIn);
            }
        }
        try {
            r.run();
        } finally {
            for (Map.Entry<String, Boolean> entry : alreadyIns.entrySet()) {
                if (!entry.getValue()) {
                    scopes.remove(entry.getKey());
                }
            }
        }
    }

    public static boolean isInScope(String name) {
        return obtainCurrentScopes().contains(name);
    }

    public static class Transmitter {
        private String[] scopes;

        public void Transmit(Runnable r) {
            RunInScope(r, scopes);
        }
    }

    public static Transmitter CreateTransmitter() {
        Transmitter trans = new Transmitter();
        trans.scopes = obtainCurrentScopes().toArray(new String[0]);
        return trans;
    }
}
