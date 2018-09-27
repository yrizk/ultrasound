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
import static android.opengl.GLES20.glVertexAttribPointer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
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

    // float[] vertexArray = new float[] {
    //     // Mallets
    //     0.0f, 0.0f,
    //     0f, -0.25f,
    //     0f,  0.25f,
    //     0.25f, 0f,
    //     -0.25f, 0f,
    //     1f, 1f,
    //     -1f, -1f
    // };
  float[] vertexArray;
  boolean[] colorData;


  public UltrasoundRenderer(Context context, Bitmap source) {
      this.context = context;
      buildVertexArray(source);
      vertexData = ByteBuffer
          .allocateDirect(vertexArray.length * BYTES_PER_FLOAT)
          .order(ByteOrder.nativeOrder())
          .asFloatBuffer();

      vertexData.put(vertexArray);
    }

    private void buildVertexArray(Bitmap b) {
      Bitmap greyscale = toGrayscale(b);
      int width = greyscale.getWidth();
      int height = greyscale.getHeight();
      vertexArray = new float[width * height * 2];
      colorData = new boolean[width * height * 2];
      int glDataCounter = 0;
      for (int i = 0; i < width; i++) {
        for (int j = 0; j < height; j++) {
          int pixel = greyscale.getPixel(i, j);
          int red = Color.red(pixel);
          int green = Color.green(pixel);
          int blue = Color.blue(pixel);
          int gray = (int)(red * 0.3 + green * 0.59 + blue * 0.11);
          int ti = i;
          int tj = j;
          if (i < width / 2) {
             ti *= -1;
          }
          if (j > height / 2) {
            tj *= -1;
          }
          float glX =  (float) ti / width / 2;
          float glY = (float) tj / height / 2;

          boolean black = gray < 127;

          vertexArray[glDataCounter] = glX;
          colorData[glDataCounter] = black;

          vertexArray[++glDataCounter] = glY;
          colorData[glDataCounter] = black;

          glDataCounter++;
        }
      }
    }


  private Bitmap toGrayscale(Bitmap bmpOriginal) {
    int width, height;
    height = bmpOriginal.getHeight();
    width = bmpOriginal.getWidth();

    Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(bmpGrayscale);
    Paint paint = new Paint();
    ColorMatrix cm = new ColorMatrix();
    cm.setSaturation(0);
    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
    paint.setColorFilter(f);
    c.drawBitmap(bmpOriginal, 0, 0, paint);
    bmpOriginal.recycle();
    return bmpGrayscale;
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

      int i = 0;
      while (i < vertexArray.length) {
        if (colorData[i]) {
          glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 1.0f);
        } else {
          glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        glDrawArrays(GL_POINTS, i, 1);
        // update correctly
        i++;
      }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
      GLES20.glViewport(0, 0, width, height);
      float ratio = (float) width / height;

      Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f);
    }
}