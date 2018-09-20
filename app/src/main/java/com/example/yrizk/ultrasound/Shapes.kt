package com.example.yrizk.ultrasound

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * A kotlin file defining the vertices of simple shapes.
 */

const val COORDS_PER_VERTEX = 3


class Triangle {

    private var program: Int = 0;


    private var triangleCoords = floatArrayOf(     // in counterclockwise order:
            0.0f, 0.622008459f, 0.0f,      // top
            -0.5f, -0.311004243f, 0.0f,    // bottom left
            0.5f, -0.311004243f, 0.0f      // bottom right
    )

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private val vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = vPosition;" +
            "}"

    private val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"

    private var mvpMatrixHandle: Int = 0;

    private var vertexBuffer: FloatBuffer =
    // (number of coordinate values * 4 bytes per float)
            ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
                // use the device hardware's native byte order
                order(ByteOrder.nativeOrder())

                // create a floating point buffer from the ByteBuffer
                asFloatBuffer().apply {
                    // add the coordinates to the FloatBuffer
                    put(triangleCoords)
                    // set the buffer to read the first coordinate
                    position(0)
                }
            }


    private var positionHandle: Int = 0;
    private var colorHandle: Int = 0;

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX;
    private val vertexStride: Int = COORDS_PER_VERTEX * 4; // stride should be the size of each vertex (4 bytes for a float)

    init {
        val vertexShader: Int = UltrasoundRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = UltrasoundRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    fun draw() {
        GLES20.glUseProgram(program) // required when starting to modify the program

        positionHandle = GLES20.glGetAttribLocation(program , "vPosition").also {
            // enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(it)

            // prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(it, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)

            colorHandle = GLES20.glGetUniformLocation(program, "vColor").also {
                // sets the color inside the gl program (colorHandle) to color
                colorHandle -> GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }

            // finally draw the triangle
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

            // disable the vertex array (note the matching call at the beginning)
            GLES20.glDisableVertexAttribArray(it)

        }
    }

    fun draw(mvpMatrix: FloatArray) {



        positionHandle = GLES20.glGetAttribLocation(program , "vPosition").also {
            // enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(it)

            // get handle to shape's transformation matrix
            mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

            // Pass the projection and view transformation to the shader
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

            // prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(it, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)

            colorHandle = GLES20.glGetUniformLocation(program, "vColor").also {
                // sets the color inside the gl program (colorHandle) to color
                colorHandle -> GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }

            // finally draw the triangle
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

            // disable the vertex array (note the matching call at the beginning)
            GLES20.glDisableVertexAttribArray(it)

        }
    }

}


class Square {

    var squareCoords = floatArrayOf(
            -0.5f,  0.5f, 0.0f,      // top left
            -0.5f, -0.5f, 0.0f,      // bottom left
            0.5f, -0.5f, 0.0f,      // bottom right
            0.5f,  0.5f, 0.0f       // top right
    )

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw the vertices

    private val vertexBuffer: FloatBuffer =
            ByteBuffer.allocateDirect(squareCoords.size * 4).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(squareCoords)
                    position(0)
                }
            }


    // initialize byte buffer for the draw list
    private val drawListBuffer: ShortBuffer =
    // (# of coordinate values * 2 bytes per short)
            ByteBuffer.allocateDirect(drawOrder.size * 2).run {
                order(ByteOrder.nativeOrder())
                asShortBuffer().apply {
                    put(drawOrder)
                    position(0)
                }
            }
}