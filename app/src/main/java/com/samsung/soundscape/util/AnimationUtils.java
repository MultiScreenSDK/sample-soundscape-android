package com.samsung.soundscape.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import com.samsung.soundscape.R;

/**
 * Created by plin on 5/15/15.
 */
public class AnimationUtils {

    public static void applyViewAnimation(Context context, View v,
                                          int animationResource,
                                          Animation.AnimationListener listener) {
        Animation animation = android.view.animation.AnimationUtils.loadAnimation(context, animationResource);
        animation.setAnimationListener(listener);
        v.startAnimation(animation);
    }

    public static void drawerAnimation(Context context, View v, Animation.AnimationListener listener) {
        applyViewAnimation(context, v, R.anim.drawer, listener);
    }

    public static void expand(Context context, View v, Animation.AnimationListener listener) {
        applyViewAnimation(context, v, R.anim.expand_library, listener);
    }

    public static void collapse(Context context, View v, Animation.AnimationListener listener) {
        applyViewAnimation(context, v, R.anim.collapse_library, listener);
    }
}
