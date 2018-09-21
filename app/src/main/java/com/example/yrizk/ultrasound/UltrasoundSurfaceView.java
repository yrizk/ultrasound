package com.example.yrizk.ultrasound;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class UltrasoundSurfaceView extends GLSurfaceView {

    private static final float TOUCH_SCALE_FACTOR = 180.f / 320;

    private final UltrasoundRenderer renderer;
    private float previousX = 0;
    private float previousY = 0;

    public UltrasoundSurfaceView(Context context) {
        super(context);
        // we're using opengles v2.0 context
        setEGLContextClientVersion(2);
        renderer = new UltrasoundRenderer();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // calculate differences
                float dx = previousX - x;
                float dy = previousY - y;

                renderer.setAngle(renderer.getAngle() + (dx + dy) * TOUCH_SCALE_FACTOR);
                requestRender();
        }

        previousX = x;
        previousY = y;
        return true;
    }
}
