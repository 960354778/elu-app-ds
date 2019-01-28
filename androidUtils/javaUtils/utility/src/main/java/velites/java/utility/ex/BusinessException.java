package velites.java.utility.ex;

/**
 * Created by regis on 2018/6/6.
 */

public class BusinessException extends CodedException {

    public BusinessException(String code, String message) {
        super(code, message);
    }

    public BusinessException(String code, String message, Throwable throwable) {
        super(code, message, throwable);
    }
}
