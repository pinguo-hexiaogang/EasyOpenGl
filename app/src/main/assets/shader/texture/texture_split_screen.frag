//分屏展示
precision mediump float;
uniform sampler2D vTexture;
varying vec2 aCoordinate;
varying vec2 aPos;
uniform float horizontalCount;
uniform float verticalCount;

void main(){

    //如果纹理设置环绕模式为GL_CLAMP_DEGE,需要判断各个情况校正坐标；如果设置为GL_REPEAT则只需要乘以相应的倍数就行了
    //        if (aPos.x<0.0 && aPos.y > 0.0){
    //            //左上角
    //            gl_FragColor = texture2D(vTexture, vec2(aCoordinate.s*2.0, aCoordinate.t *2.0 - 1.0));
    //        }
    //        else if (aPos.x >0.0 && aPos.y > 0.0){
    //            //右上角
    //            gl_FragColor = texture2D(vTexture, vec2(aCoordinate.s*2.0 - 1.0, aCoordinate.t *2.0 -1.0));
    //
    //
    //        } else if (aPos.x <0.0 && aPos.y < 0.0){
    //            //左下角
    //            gl_FragColor = texture2D(vTexture, vec2(aCoordinate.s*2.0, aCoordinate.t *2.0 ));
    //
    //        } else {
    //            //右下角
    //            gl_FragColor = texture2D(vTexture, vec2(aCoordinate.s*2.0 - 1.0, aCoordinate.t *2.0 ));
    //        }
    gl_FragColor = texture2D(vTexture, vec2(aCoordinate.s*horizontalCount, aCoordinate.t*verticalCount));


}