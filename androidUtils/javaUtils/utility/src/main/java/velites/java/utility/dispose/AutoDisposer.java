package velites.java.utility.dispose;

/**
 * 
 * @author regis
 * 
 *         A container-like object to executing objects disposing during
 *         IAutoDisposeHost.disposeHost is executing. This object should
 *         recognize which objects' life scope is corresponding to passed
 *         IAutoDisposeHost object and then dispose them.
 */
public interface AutoDisposer {
    /**
     * 
     * @param host
     *            Filter for host (null for clear all).
     * @return Count of got removed.
     */
    int dispose(AutoDisposeHost host);
}