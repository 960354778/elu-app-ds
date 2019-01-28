package velites.java.utility.ex;

/**
 * Created by regis on 2018/6/12.
 */

public class StatedException extends RuntimeException {
    private final Object state;

    public StatedException(Throwable throwable, Object state) {
        super(throwable);
        this.state = state;
    }

    public Object getState() {
        return state;
    }
}
