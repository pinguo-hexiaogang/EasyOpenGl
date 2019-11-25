attribute vec4 vPosition;
attribute vec2 vTexturePos;
uniform mat4 vMatrix;
varying vec2 aCoordinate;
varying vec2 aPos;
void main() {
    gl_Position = vMatrix*vPosition;
    aCoordinate = vTexturePos;
    aPos = vec2(vPosition.x,vPosition.y);
}

