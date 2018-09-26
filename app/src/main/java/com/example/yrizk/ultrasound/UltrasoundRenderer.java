package com.example.yrizk.ultrasound;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.*;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import com.example.yrizk.ultrasound.util.ResourceReader;
import com.example.yrizk.ultrasound.util.Shaders;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public  class UltrasoundRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "UltrasoundRenderer";
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;

    private int program = 0;

    private int uColorLocation;
    private int aPositionLocation;
    private int mvpMatrixHandle;

    private FloatBuffer vertexData;

    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private Context context;
    float[] malletData = new float[] {
        // Mallets
        0.0f, 0.0f,
        0f, -0.25f,
        0f,  0.25f,
        0.25f, 0f,
        -0.25f, 0f,
        1f, 1f,
        -1f, -1f
    };

    public UltrasoundRenderer(Context context) {
      this.context = context;


      vertexData = ByteBuffer
          .allocateDirect(malletData.length * BYTES_PER_FLOAT)
          .order(ByteOrder.nativeOrder())
          .asFloatBuffer();

      vertexData.put(malletData);

    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderSource = ResourceReader.readFromResource(context, R.raw.vertex_shader);
        String fragmentShaderSource = ResourceReader.readFromResource(context, R.raw.fragment_shader);

        int vertexShader = Shaders.compileVertexShader(vertexShaderSource);
        int fragmentShader = Shaders.compileFragmentShader(fragmentShaderSource);

        program = Shaders.linkProgram(vertexShader, fragmentShader);


        Shaders.validateProgram(program);

        glUseProgram(program);

        uColorLocation = glGetUniformLocation(program, "u_Color");

        aPositionLocation = glGetAttribLocation(program, "a_Position");

        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_POSITION_LOCATION.
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
            false, 0, vertexData);

        glEnableVertexAttribArray(aPositionLocation);
    }

    public void onDrawFrame(GL10 unused) {
      // Clear the rendering surface.
      glClear(GL_COLOR_BUFFER_BIT);

      // Set the camera position (View matrix)
      Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

      // Calculate the projection and view transformation
      Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

      // get handle to shape's transformation matrix
      mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");

      // Pass the projection and view transformation to the shader
      GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

      // Draw the first mallet blue.
      for (int i = 0; i < malletData.length / 2 /* the stride */; i++) {
        if (i % 2 == 0) {
          glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
          glDrawArrays(GL_POINTS, i, 1);
        }
        else {
          // Draw the second mallet red.
          glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
          glDrawArrays(GL_POINTS, i, 1);
        }
      }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
      GLES20.glViewport(0, 0, width, height);
      float ratio = (float) width / height;

      Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f);
    }
}