precision mediump float;
uniform sampler2D vTexture;
varying vec2 aCoordinate;
uniform int isHalf;
varying vec2 aPos;
void main() {
    vec4 color = texture2D(vTexture, aCoordinate);
    float c = color.r * 0.3 + color.g * 0.59 + color.b * 0.11;
    gl_FragColor = vec4(c,c,c,1.0);
}