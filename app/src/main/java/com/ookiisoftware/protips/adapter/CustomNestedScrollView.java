package com.ookiisoftware.protips.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

public class CustomNestedScrollView extends NestedScrollView {

    private boolean scrollable = true;

    public CustomNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public CustomNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return scrollable && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return scrollable && super.onInterceptTouchEvent(ev);
    }

    public void setScrollingEnabled(boolean enabled) {
        scrollable = enabled;
    }
}
