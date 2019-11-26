package com.denny.easyopengl.render

import android.opengl.GLES20
import com.denny.easyopengl.painter.IPainter
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class EasyRender : IRender {

    private var _painter: IPainter? = null
    private var width: Int = 0
    private var height: Int = 0

    override fun setPainter(painter: IPainter) {
        _painter = painter
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    }

    override fun onDrawFrame(gl: GL10?) {
        _painter?.ifNeedInit(width,height)
        _painter?.draw(gl)
    }
}