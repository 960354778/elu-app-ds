package com.qingyun.zhiyunelu.ds.op;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * ================================================
 * 作    者：tlf_2
 * 个人邮箱：
 * 创建日期：2019/10/23 15:14
 * 描    述：
 * 修订历史：
 * ================================================
 */


public class GrayScaleImageView extends AppCompatImageView {

    public GrayScaleImageView(Context context) {
        super(context);
    }

    public GrayScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GrayScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Drawable drawable = getDrawable();
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        if (drawable != null) {
            // drawable.mutate使得此drawable共享状态，改变时全部改变
            drawable.mutate().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }else {
            drawable.mutate().clearColorFilter();
        }
        break;
        case MotionEvent.ACTION_MOVE:
        break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
        if (drawable != null) {
            drawable.mutate().clearColorFilter();
        }
        break;
    }
     return super.onTouchEvent(event);
    }
}
