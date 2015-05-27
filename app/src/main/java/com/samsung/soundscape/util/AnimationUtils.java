/*******************************************************************************
 * Copyright (c) 2015 Samsung Electronics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/

package com.samsung.soundscape.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import com.samsung.soundscape.R;

/**
 * Created by plin on 5/15/15.
 */
public class AnimationUtils {

    /**
     * Apply the specified animation to the view.
     *
     * @param context           the context
     * @param v                 the view to apply the animation to
     * @param animationResource the animation resource to apply
     * @param listener          the animation listener to attach to the animation
     */
    public static void applyViewAnimation(Context context, View v,
                                          int animationResource,
                                          Animation.AnimationListener listener) {
        Animation animation = android.view.animation.AnimationUtils.loadAnimation(context, animationResource);
        animation.setAnimationListener(listener);
        v.startAnimation(animation);
    }

    /**
     * Perform the drawer animation on the view.
     *
     * @param context   the context
     * @param v         the view to apply the drawer animation on
     * @param listener  the animation listener to attach to the animation
     */
    public static void drawerAnimation(Context context, View v, Animation.AnimationListener listener) {
        applyViewAnimation(context, v, R.anim.drawer, listener);
    }

    /**
     * Perform the expand animation on the view.
     *
     * @param context   the context
     * @param v         the view to apply the expand animation on
     * @param listener  the animation listener to attach to the animation
     */
    public static void expand(Context context, View v, Animation.AnimationListener listener) {
        applyViewAnimation(context, v, R.anim.expand_library, listener);
    }

    /**
     * Perform the collapse animation on the view.
     *
     * @param context   the context
     * @param v         the view to apply the collapse animation on
     * @param listener  the animation listener to attach to the animation
     */
    public static void collapse(Context context, View v, Animation.AnimationListener listener) {
        applyViewAnimation(context, v, R.anim.collapse_library, listener);
    }
}
