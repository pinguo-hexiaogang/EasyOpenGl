/*
 *
 * ShapeActivity.java
 * 
 * Created by Wuwang on 2016/9/30
 */
package com.denny.easyopengl.activity

import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.denny.easyopengl.R
import com.denny.easyopengl.painter.ComposePainter
import com.denny.easyopengl.painter.IPainter
import com.denny.easyopengl.painter.player.YuvPlayerMotionPainter
import com.denny.easyopengl.painter.player.YuvPlayerPainter
import com.denny.easyopengl.painter.shape.CirclePainter
import com.denny.easyopengl.painter.shape.TrianglePainter
import com.denny.easyopengl.painter.texture.TexturePainter
import com.denny.easyopengl.painter.texture.TextureSplitPainter
import com.denny.easyopengl.render.EasyRender

/**
 * Description:
 */
class YuvPlayerActivity : AppCompatActivity(), View.OnClickListener {

    private var mGLView: GLSurfaceView? = null
    private val render = EasyRender()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fglview)
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
            add(EntryItem("yuv player", YuvPlayerPainter::class.java))
            add(EntryItem("yuv player motion", ComposePainter::class.java))
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val painterClass = data?.getSerializableExtra("name") as? Class<*>
            val painter = painterClass?.newInstance() as? IPainter
            if(painter is ComposePainter){
                painter.addPinters(listOf(YuvPlayerPainter(),YuvPlayerMotionPainter()))
            }
            render.setPainter(painter!!)
            mGLView?.queueEvent {
                mGLView?.requestRender()
            }
        }
    }

    companion object {

        private val REQ_CHOOSE = 0x0101
    }
}
