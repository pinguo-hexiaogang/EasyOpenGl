/*
 *
 * ChooseActivity.java
 * 
 * Created by Wuwang on 2016/10/13
 */
package com.denny.easyopengl.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.denny.easyopengl.R
import com.denny.easyopengl.painter.shape.CirclePainter
import com.denny.easyopengl.painter.shape.ConePainter
import com.denny.easyopengl.painter.shape.SquarePainter
import com.denny.easyopengl.painter.shape.TrianglePainter
import com.denny.easyopengl.painter.texture.TexturePainter
import java.util.*

/**
 * Description:
 */
class ChooseActivity : AppCompatActivity() {

    private var context: ChooseActivity? = null
    private var mList: ListView? = null
    private var mData: ArrayList<Data>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_list)
        init()
    }

    private fun init() {
        initData()
        mList = findViewById(R.id.mList) as ListView
        mList!!.adapter = Adapter()
        mList!!.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val intent = Intent()
                intent.putExtra("name", mData!![position].clazz)
                setResult(RESULT_OK, intent)
                finish()
            }
    }

    private fun initData() {
        mData = ArrayList()
        add("三角形", TrianglePainter::class.java)
//        add("正三角形", TriangleWithCamera::class.java)
//        add("彩色三角形", TriangleColorFull::class.java)
//        add("正方形", Square::class.java)
        add("圆形", CirclePainter::class.java)
        add("正方体", SquarePainter::class.java)
        add("圆锥", ConePainter::class.java)
        add("纹理", TexturePainter::class.java)
//        add("圆柱", Cylinder::class.java)
//        add("球体", Ball::class.java)
//        add("带光源的球体", BallWithLight::class.java)
    }

    private fun add(showName: String, clazz: Class<*>) {
        val data = Data()
        data.clazz = clazz
        data.showName = showName
        mData!!.add(data)
    }


    private inner class Data {
        internal var showName: String? = null
        internal var clazz: Class<*>? = null
    }

    private inner class Adapter : BaseAdapter() {

        override fun getCount(): Int {
            return mData!!.size
        }

        override fun getItem(position: Int): Any {
            return mData!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView =
                    LayoutInflater.from(context).inflate(R.layout.item_choose, parent, false)
                convertView!!.tag = ViewHolder(convertView)
            }
            val holder = convertView.tag as ViewHolder
            holder.setData(mData!![position])
            return convertView
        }

        private inner class ViewHolder(parent: View) {
            private val mName: TextView

            init {
                mName = parent.findViewById(R.id.mName) as TextView
            }

            fun setData(data: Data) {
                mName.text = data.showName
            }
        }
    }


}
