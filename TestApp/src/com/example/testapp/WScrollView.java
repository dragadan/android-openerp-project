package com.example.testapp;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.view.MotionEvent;
import android.content.Context;
import android.util.AttributeSet;

public class WScrollView extends HorizontalScrollView
{
    public ScrollView sv;
    public WScrollView(Context context)
    {
        super(context);
    }

    public WScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override public boolean onTouchEvent(MotionEvent event)
    {
        boolean ret = super.onTouchEvent(event);
        ret = ret | sv.onTouchEvent(event);
        return ret;
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent event)
    {
        boolean ret = super.onInterceptTouchEvent(event);
        ret = ret | sv.onInterceptTouchEvent(event);
        return ret;
    }
}