precision mediump float;
varying vec2 vTextCoord;
uniform sampler2D texture0;
uniform sampler2D texture1;
uniform sampler2D texture2;
uniform sampler2D texture3;
uniform sampler2D texture4;
uniform sampler2D texture5;

void main(){
    // 0 is the newest image and 5 is the oldest
    vec4 blur0 = texture2D(texture0, vTextCoord);
    vec4 blur1 = texture2D(texture1, vTextCoord);
    vec4 blur2 = texture2D(texture2, vTextCoord);
    vec4 blur3 = texture2D(texture3, vTextCoord);
    vec4 blur4 = texture2D(texture4, vTextCoord);
    vec4 blur5 = texture2D(texture5, vTextCoord);

    vec4 summedBlur = blur0 + blur1 + blur2 +blur3 + blur4 + blur5;
    gl_FragColor = vec4(summedBlur.r * 0.166,summedBlur.g * 0.166,summedBlur.b * 0.166,summedBlur.a * 1.0);
    //gl_FragColor = vec4(0.0, 1.0, 0.0, 0.0);
}