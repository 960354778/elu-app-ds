package velites.java.utility.generic;

/**
 * Created by regis on 17/4/20.
 */

public class Tuple3<T1, T2, T3> {
    public T1 v1;
    public T2 v2;
    public T3 v3;

    public Tuple3(T1 v1, T2 v2, T3 v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }
}
