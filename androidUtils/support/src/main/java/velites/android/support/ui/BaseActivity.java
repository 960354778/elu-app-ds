package velites.android.support.ui;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import velites.android.support.R;

/**
 * Created by regis on 16/11/14.
 */
public class BaseActivity extends RxAppCompatActivity {

    protected static final long DEFAULT_EXIT_PENDING_MILLS = 2000;

    private Long lastBackPressedAt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected boolean isAtLast() {
        return false;
    }

    protected long getExitPendingMills() {
        return DEFAULT_EXIT_PENDING_MILLS;
    }

    @Override
    public void onBackPressed() {
        if (isAtLast()) {
            long cur = SystemClock.uptimeMillis();
            if (lastBackPressedAt != null && cur - lastBackPressedAt < getExitPendingMills()) {
                super.onBackPressed();
            }
            lastBackPressedAt = cur;
            Toast.makeText(this, R.string.hint_double_back_to_exit, Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    protected final void clearBackPressedRecord() {
        lastBackPressedAt = null;
    }
}
