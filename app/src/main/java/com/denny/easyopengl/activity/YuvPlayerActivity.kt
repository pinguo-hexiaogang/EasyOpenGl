/*
 *
 * ShapeActivity.java
 * 
 * Created by Wuwang on 2016/9/30
 */
package com.denny.easyopengl.activity

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.denny.easyopengl.R
import com.denny.easyopengl.painter.player.YuvPlayerPainter
import com.denny.easyopengl.render.EasyRender

/**
 * Description:
 */
class YuvPlayerActivity : AppCompatActivity() {

    private var mGLView: GLSurfaceView? = null
    private val render = EasyRender()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glview)
        init()
    }

    private fun init() {
        mGLView = findViewById<GLSurfaceView>(R.id.mGLView)
        mGLView?.setEGLContextClientVersion(2)
        mGLView?.setRenderer(render)
        render.setPainter(YuvPlayerPainter())
        mGLView?.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }


    override fun onResume() {
        super.onResume()
        mGLView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mGLView!!.onPause()
    }

}
