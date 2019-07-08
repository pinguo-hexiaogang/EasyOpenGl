package com.denny.easyopengl.render

import android.opengl.GLSurfaceView
import com.denny.easyopengl.painter.IPainter

interface IRender : GLSurfaceView.Renderer {
    fun setPainter(painter:IPainter)
}