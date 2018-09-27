package com.example.yrizk.ultrasound;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

public class UltrasoundSurfaceView extends GLSurfaceView {

    private static final float TOUCH_SCALE_FACTOR = 180.f / 320;

    private final UltrasoundRenderer renderer;
    private float previousX = 0;
    private float previousY = 0;

    public UltrasoundSurfaceView(Context context, Bitmap source) {
        super(context);
        // we're using opengles v2.0 context
        setEGLContextClientVersion(2);
        renderer = new UltrasoundRenderer(context, source);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
