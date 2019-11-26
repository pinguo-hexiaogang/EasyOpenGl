precision mediump float;
attribute vec4 aPosition;
attribute vec2 aTexturePos;
uniform mat4 uMatrix;
varying vec2 vTextCoord;
void main(){
    gl_Position = uMatrix * aPosition;
    vTextCoord = vec2(aTexturePos.x, 1.0-aTexturePos.y);
    //vTextCoord = aTexturePos;
}