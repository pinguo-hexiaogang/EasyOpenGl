package com.denny.easyopengl.painter.shape

import android.opengl.GLES20
import android.opengl.Matrix
import com.denny.easyopengl.EasyApplication
import com.denny.easyopengl.painter.IPainter
import com.denny.easyopengl.util.AssetsUtils
import com.denny.easyopengl.util.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

/**
 * 圆锥
 */
class ConePainter : IPainter {
    private var inited = false
    private var width = 0
    private var height = 0
    private val vertexShader =
        AssetsUtils.getAssetsFileContent(
            EasyApplication.application,
            "shader/cone/cone_vertex_shader"
        )
    private val fragmentShader = AssetsUtils.getAssetsFileContent(
        EasyApplication.application,
        "shader/cone/cone_fragment_shader"
    )
    private var program: Int = 0
    private var points = mutableListOf<Float>()
    private var floatBuffer: FloatBuffer? = null

    private var matrixUniformIndex: Int = 0
    private var positionAttrIndex: Int = 0

    private var matrix = FloatArray(16)
    private val circle = CirclePainter(CONE_RADIUS)

    companion object {
        const val CONE_HEIGHT = 3F
        const val CONE_RADIUS = 2F
        const val POINT_COUNT = 180
    }


    override fun ifNeedInit(width: Int, height: Int) {
        circle.ifNeedInit(width,height)
        if (!inited) {
            inited = true
            program = ShaderUtil.createShaderProgram(vertexShader, fragmentShader)
            assemblePoints()
            val byteBuffer = ByteBuffer.allocateDirect(points.size * 4)
            byteBuffer.order(ByteOrder.nativeOrder())
            floatBuffer = byteBuffer.asFloatBuffer()
            floatBuffer?.put(points.toFloatArray())
            floatBuffer?.position(0)
            matrixUniformIndex = GLES20.glGetUniformLocation(program, "vMatrix")
            positionAttrIndex = GLES20.glGetAttribLocation(program, "vPosition")
            //colorUniformIndex = GLES20.glGetUniformLocation(program, "vColor")
        }
        if (this.width != width || this.height != height) {
            this.width = width
            this.height = height
            val projectionM = FloatArray(16)
            val viewM = FloatArray(16)
            Matrix.perspectiveM(projectionM, 0, 60f, width * 1.0f / height, 1f, 20f)
            Matrix.setLookAtM(viewM, 0, 3f, 10f, 15f, 0f, 0f, 0f, 0f, 1f, 0f)
            Matrix.multiplyMM(matrix, 0, projectionM, 0, viewM, 0)
            circle.setMatrix(matrix)
        }
    }

    private fun assemblePoints() {
        //最底部的点
        points.add(0f)
        points.add(0f)
        points.add(CONE_HEIGHT)
        val step = 360f / POINT_COUNT
        var corner = 0f
        while (corner < 360 + step) {
            var x = CONE_RADIUS * sin(corner * Math.PI / 180f).toFloat()
            var y = CONE_RADIUS * cos(corner * Math.PI / 180f).toFloat()
            points.add(x)
            points.add(y)
            points.add(0f)
            corner += step
        }

    }

    override fun draw(gl: GL10?) {
        GLES20.glUseProgram(program)
        GLES20.glClearColor(1f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glEnableVertexAttribArray(positionAttrIndex)
        GLES20.glVertexAttribPointer(
            positionAttrIndex,
            3,
            GLES20.GL_FLOAT,
            false,
            3 * 4,
            floatBuffer!!
        )
        //GLES20.glUniform4fv(colorUniformIndex, 1, color, 0)
        GLES20.glUniformMatrix4fv(matrixUniformIndex, 1, false, matrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, points.size / 3)
        GLES20.glDisableVertexAttribArray(positionAttrIndex)
        circle.draw(gl)
    }
}