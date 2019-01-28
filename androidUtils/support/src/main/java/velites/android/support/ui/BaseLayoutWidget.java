package velites.android.support.ui;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import velites.android.support.R;
import velites.android.support.R2;

public abstract class BaseLayoutWidget {

    protected BaseLayoutWidget() {}
    protected BaseLayoutWidget(Activity source) {
        this.bind(source);
    }
    protected BaseLayoutWidget(View source) {
        this.bind(source);
    }

    public void bind(Activity source) {
        ButterKnife.bind(this, source);
    }

    public void bind(View source) {
        ButterKnife.bind(this, source);
    }

    public static class List extends BaseLayoutWidget {
        @BindView(R2.id.list_body)
        RecyclerView list;

        public List() {
            super();
        }

        public List(Activity source) {
            super(source);
        }

        public RecyclerView getList() {
            return list;
        }
    }
}
