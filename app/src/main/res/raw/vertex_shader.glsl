attribute vec4 a_Position;
uniform mat4 uMVPMatrix;

void main()
{
    gl_Position = uMVPMatrix * a_Position;
    gl_PointSize = 10.0;
}