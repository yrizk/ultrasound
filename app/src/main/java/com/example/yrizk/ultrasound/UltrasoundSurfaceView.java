package com.example.yrizk.ultrasound;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class UltrasoundSurfaceView extends GLSurfaceView {

    public UltrasoundSurfaceView(Context context) {
        super(context);
        setRenderer(new UltrasoundRenderer());
    }


    public UltrasoundSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRenderer(new UltrasoundRenderer());
    }

    private static final class UltrasoundRenderer implements Renderer {

        private static final String TAG = "UltrasoundRenderer";

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.d(TAG, "onSurfaceCreated");
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.d(TAG, "onSurfaceChanged");
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            Log.d(TAG, "onDrawFrame");

        }
    }
}
