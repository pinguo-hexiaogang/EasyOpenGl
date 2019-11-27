/*
 *
 * ShapeActivity.java
 * 
 * Created by Wuwang on 2016/9/30
 */
package com.denny.easyopengl.activity

import android.content.Context
import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.denny.easyopengl.R
import com.denny.easyopengl.painter.IPainter
import com.denny.easyopengl.painter.shape.*
import com.denny.easyopengl.painter.texture.TexturePainter
import com.denny.easyopengl.painter.texture.TextureSplitPainter
import com.denny.easyopengl.render.EasyRender

/**
 * Description:
 */
class ShapeActivity : AppCompatActivity(), View.OnClickListener {

    private var mChange: Button? = null
    private var mGLView: GLSurfaceView? = null
    private val render = EasyRender()


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
                intent.putExtra(ChooseActivity.DATAS, createEntries())
                startActivityForResult(intent, REQ_CHOOSE)
            }
        }
    }

    private fun createEntries(): ArrayList<EntryItem> {
        return arrayListOf<EntryItem>().apply {
            add(EntryItem("三角形", TrianglePainter::class.java))
//        add(EntryItem("正三角形", TriangleWithCamera::class.java))
//        add(EntryItem("彩色三角形", TriangleColorFull::class.java))
            add(EntryItem("正方体", CubePainter::class.java))
            add(EntryItem("圆形", CirclePainter::class.java))
            add(EntryItem("正方形", SquarePainter::class.java))
            add(EntryItem("圆锥", ConePainter::class.java))
//            add(EntryItem("圆柱", Cylinder::class.java))
//            add(EntryItem("球体", Ball::class.java))
//            add(EntryItem("带光源的球体", BallWithLight::class.java))
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
