/*
 *
 * ShapeActivity.java
 * 
 * Created by Wuwang on 2016/9/30
 */
package com.denny.easyopengl.activity

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.denny.easyopengl.R
import com.denny.easyopengl.painter.shape.CubePainter
import com.denny.easyopengl.render.FboRender

/**
 * Description:
 */
class FBOActivity : AppCompatActivity() {

    private var mGLView: GLSurfaceView? = null
    private val render = FboRender()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glview)
        init()
    }

    private fun init() {
        mGLView = findViewById(R.id.mGLView) as GLSurfaceView
        mGLView?.setEGLContextClientVersion(2)
        mGLView?.setRenderer(render)
        render.setPainter(CubePainter())
        mGLView?.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        mGLView?.queueEvent {
            mGLView?.requestRender()
        }
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
