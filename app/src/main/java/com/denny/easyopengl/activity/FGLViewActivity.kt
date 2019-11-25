/*
 *
 * FGLViewActivity.java
 * 
 * Created by Wuwang on 2016/9/30
 */
package com.denny.easyopengl.activity

import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.denny.easyopengl.R
import com.denny.easyopengl.painter.IPainter
import com.denny.easyopengl.painter.shape.TrianglePainter
import com.denny.easyopengl.render.ShapeRender

/**
 * Description:
 */
class FGLViewActivity : AppCompatActivity(), View.OnClickListener {

    private var mChange: Button? = null
    private var mGLView: GLSurfaceView? = null
    private val render = ShapeRender()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fglview)
        init()
    }

    private fun init() {
        mChange = findViewById(R.id.mChange) as Button
        mGLView = findViewById(R.id.mGLView) as GLSurfaceView
        mGLView?.setEGLContextClientVersion(2)
        mGLView?.setRenderer(render)
        render.setPainter(TrianglePainter())
        mGLView?.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        mGLView?.queueEvent {
            mGLView?.requestRender()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mChange -> {
                val intent = Intent(this, ChooseActivity::class.java)
                startActivityForResult(intent, REQ_CHOOSE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val painterClass = data?.getSerializableExtra("name") as? Class<*>
            val painter = painterClass?.newInstance() as? IPainter
            render.setPainter(painter!!)
            mGLView?.queueEvent {
                mGLView?.requestRender()
            }
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

    companion object {

        private val REQ_CHOOSE = 0x0101
    }
}
