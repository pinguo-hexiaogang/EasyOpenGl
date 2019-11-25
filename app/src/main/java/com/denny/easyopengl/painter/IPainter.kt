package com.denny.easyopengl.painter

import javax.microedition.khronos.opengles.GL10

interface IPainter {
    /**
     * 不需要每帧都处理的逻辑
     */
    fun ifNeedInit(width: Int, height: Int)
    fun draw(gl: GL10?)
    /**
     * painter继承场景,供子painter配置以及画一些东西
     */
    fun subDraw(gl: GL10?, program: Int){

    }
}