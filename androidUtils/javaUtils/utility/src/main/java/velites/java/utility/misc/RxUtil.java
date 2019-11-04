package velites.java.utility.misc;

import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import velites.java.utility.generic.Action0;
import velites.java.utility.generic.Func0;
import velites.java.utility.generic.Func1;
import velites.java.utility.log.LogStub;

public final class RxUtil {
    private RxUtil() {}

    public static void handleRxExceptionByDefault(Throwable ex) {
        ExceptionUtil.swallowThrowable(ex, LogStub.LOG_LEVEL_WARNING, RxUtil.class);
    }

    public static final Consumer<Throwable> simpleErrorConsumer = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable ex) throws Exception {
            handleRxExceptionByDefault(ex);
        }
    };

    public static ObservableOnSubscribe<Integer> buildSimpleActionObservable(Action0 act) {
        return emitter -> {
            if (act != null) {
                act.a();
            }
            emitter.onNext(0);
        };
    }

    public static <T> ObservableOnSubscribe<T> buildSimpleFuncObservable(Func0<T> func) {
        return emitter -> {
            if (func != null) {
                emitter.onNext(func.f());
            }
        };
    }

    public static class ObserverDelegate<TSource, TTarget> implements Observer<TSource> {

        private final Observer<TTarget> inner;
        private final Func1<TSource, TTarget> converter;

        public ObserverDelegate(Observer<TTarget> inner, Func1<TSource, TTarget> converter) {
            ExceptionUtil.assertArgumentNotNull(inner, "inner");
            this.inner = inner;
            this.converter = converter == null ? s -> (TTarget)s : converter;
        }

        @Override
        public void onSubscribe(Disposable d) {
            this.inner.onSubscribe(d);
        }

        @Override
        public void onNext(TSource c) {
            this.inner.onNext(converter.f(c));
        }

        @Override
        public void onError(Throwable e) {
            this.inner.onError(e);
        }

        @Override
        public void onComplete() {
            this.inner.onComplete();
        }
    }



}
