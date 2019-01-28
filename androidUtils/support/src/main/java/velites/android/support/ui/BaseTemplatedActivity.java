package velites.android.support.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import velites.android.support.R;
import velites.android.utility.framework.HierarchyHelper;

public abstract class BaseTemplatedActivity extends BaseActivity {

    protected int getHeaderToolbarResId() {
        return R.id.header_toolbar;
    }
    protected int getHeaderMoreResId() {
        return R.id.header_more;
    }
    protected int getBodyResId() {
        return R.id.body;
    }
    protected abstract int getTemplateResId();
    protected Integer getContentResId() {
        return null;
    }
    protected Integer getHeadMoreContentResId() {
        return null;
    }
    protected int[] getMoveToHeadMoreViewIds() {
        return null;
    }
    protected Integer getMenuResId() {
        return null;
    }

    private Toolbar toolbar;
    private View body;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getTemplateResId());
        toolbar = (Toolbar) this.findViewById(this.getHeaderToolbarResId());
        ViewGroup more = (ViewGroup) this.findViewById(this.getHeaderMoreResId());
        ViewGroup rawBody = (ViewGroup) this.findViewById(this.getBodyResId());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(isDisplayShowTitleForAppName());
        Integer rid = getHeadMoreContentResId();
        if (rid != null) {
            getLayoutInflater().inflate(rid, more, true);
        }
        rid = getContentResId();
        if (rid != null) {
            body = getLayoutInflater().inflate(rid, rawBody, true);
            HierarchyHelper.moveViews(body, more, getMoveToHeadMoreViewIds());
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Integer mId = this.getMenuResId();
        if (mId != null) {
            getMenuInflater().inflate(mId, menu);
            int[] ids = getInvisibleMenuIds();
            if (ids != null) {
                for (int id : ids) {
                    MenuItem item = menu.findItem(id);
                    if (item == null) {
                        menu.setGroupVisible(id, false);
                    } else {
                        item.setVisible(false);
                    }
                }
            }
            ids = getVisibleMenuIds();
            if (ids != null) {
                for (int id : ids) {
                    MenuItem item = menu.findItem(id);
                    if (item == null) {
                        menu.setGroupVisible(id, true);
                    } else {
                        item.setVisible(true);
                    }
                }
            }
            return true;
        }
        return false;
    }

    protected int[] getInvisibleMenuIds() {
        return null;
    }
    protected int[] getVisibleMenuIds() {
        return null;
    }
    protected boolean isDisplayShowTitleForAppName(){
        return false;
    }
}
