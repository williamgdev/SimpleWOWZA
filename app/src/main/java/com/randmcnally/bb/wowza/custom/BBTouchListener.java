package com.randmcnally.bb.wowza.custom;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.randmcnally.bb.wowza.activity.ChannelActivity;

public abstract class BBTouchListener implements View.OnTouchListener {

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int maskedAction = event.getActionMasked();
        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                oNTouchStart();
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                oNTouchEnd();
                return true;

        }

        return false;
    }

    public abstract void oNTouchStart();
    public abstract void oNTouchEnd();
}
