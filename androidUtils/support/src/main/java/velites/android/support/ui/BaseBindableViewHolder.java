package velites.android.support.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by regis on 16/12/8.
 */

public abstract class BaseBindableViewHolder<TItem> extends RecyclerView.ViewHolder {
    protected BaseBindableViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindItem(TItem item);
}
