package com.samsung.android.graphics.spr.animation.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

public class QuadEaseIn implements Interpolator {
    public QuadEaseIn(Context context, AttributeSet attributeSet) {
    }

    private float in(float f) {
        return f * f;
    }

    public float getInterpolation(float f) {
        return in(f);
    }
}
