package velites.java.utility.dispose;

/**
 * 
 * @author regis
 * 
 *         Represents a life scope of specified objects, which should be
 *         disposed during disposeHost executing.
 */
public interface AutoDisposeHost {
    void disposeHost(boolean preferInSync);

    class SimpleImplementation implements AutoDisposeHost {
        @Override
        public void disposeHost(boolean preferInSync) {
            AutoDisposeHub.disposeAll(this);
        }
    }
}
