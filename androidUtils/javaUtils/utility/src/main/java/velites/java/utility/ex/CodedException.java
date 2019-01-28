package velites.java.utility.ex;

/**
 * Created by regis on 2018/6/6.
 */

public class CodedException extends RuntimeException {

    private final String code;
    public String getCode() {
        return code;
    }

    public CodedException(String code, String message) {
        super(message);
        this.code = code;
    }

    public CodedException(String code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }
}
