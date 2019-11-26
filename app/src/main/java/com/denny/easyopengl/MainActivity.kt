package com.denny.easyopengl

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denny.easyopengl.activity.ShapeActivity
import com.denny.easyopengl.activity.TextureActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var data: ArrayList<MenuBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler_view.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        data = ArrayList()
        add("绘制形体", ShapeActivity::class.java)
        add("纹理", TextureActivity::class.java)
//        add("图形变换", VaryActivity::class.java)
//        add("相机", CameraActivity::class.java)
//        add("相机2 动画", Camera2Activity::class.java)
//        add("相机3 美颜", Camera3Activity::class.java)
//        add("压缩纹理动画", ZipActivity::class.java)
//        add("FBO使用", FBOActivity::class.java)
//        add("EGL后台处理", EGLBackEnvActivity::class.java)
//        add("3D obj模型", ObjLoadActivity::class.java)
//        add("obj+mtl模型", ObjLoadActivity2::class.java)
//        add("VR效果", VrContextActivity::class.java)
//        add("颜色混合", BlendActivity::class.java)
//        add("光照", LightActivity::class.java)
        recycler_view.adapter = MenuAdapter()
    }


    private fun add(name: String, clazz: Class<*>) {
        val bean = MenuBean()
        bean.name = name
        bean.clazz = clazz
        data!!.add(bean)
    }

    private inner class MenuBean {

        internal var name: String? = null
        internal var clazz: Class<*>? = null

    }

    private inner class MenuAdapter : RecyclerView.Adapter<MenuAdapter.MenuHolder>() {

        override fun getItemCount(): Int {
            return data!!.size
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
            return MenuHolder(layoutInflater.inflate(R.layout.item_button, parent, false))
        }

        override fun onBindViewHolder(holder: MenuHolder, position: Int) {
            holder.position = position
        }

        internal inner class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private val mBtn: Button = itemView.findViewById(R.id.mBtn) as Button

            init {
                mBtn.setOnClickListener(this@MainActivity)
            }

            fun setPosition(position: Int) {
                val bean = data!![position]
                mBtn.text = bean.name
                mBtn.tag = position
            }
        }

    }

    override fun onClick(view: View) {
        val position = view.tag as Int
        val bean = data!![position]
        startActivity(Intent(this, bean.clazz))
    }
}
