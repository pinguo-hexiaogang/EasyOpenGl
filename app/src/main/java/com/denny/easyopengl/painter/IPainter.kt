package com.denny.easyopengl.painter

import javax.microedition.khronos.opengles.GL10

interface IPainter {
    fun ifNeedInit(width: Int, height: Int)
    fun draw(gl: GL10?)
}