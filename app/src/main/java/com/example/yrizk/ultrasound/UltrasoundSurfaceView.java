package com.example.yrizk.ultrasound;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class UltrasoundSurfaceView extends GLSurfaceView {

    public UltrasoundSurfaceView(Context context) {
        super(context);
        // we're using opengles v2.0 context
        setEGLContextClientVersion(2);
        setRenderer(new UltrasoundRenderer());
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
