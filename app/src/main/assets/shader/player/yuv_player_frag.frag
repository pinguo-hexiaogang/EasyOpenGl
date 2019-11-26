precision mediump float;
varying vec2 vTextCoord;
uniform sampler2D yTexture;
uniform sampler2D uTexture;
uniform sampler2D vTexture;

void main(){
    vec3 yuv;
    vec3 rgb;
    //分别取yuv各个分量的采样纹理（r表示？）
    //
    yuv.x = texture2D(yTexture, vTextCoord).b;
    yuv.y = texture2D(uTexture, vTextCoord).b - 0.5;
    yuv.z = texture2D(vTexture, vTextCoord).b - 0.5;
    rgb = mat3(
    1.0, 1.0, 1.0,
    0.0, -0.39465, 2.03211,
    1.13983, -0.5806, 0.0
    ) * yuv;
    //gl_FragColor是OpenGL内置的
    gl_FragColor = vec4(rgb, 1.0);
}