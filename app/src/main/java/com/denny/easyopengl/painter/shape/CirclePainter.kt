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

class CirclePainter : IPainter {
    private val vertexShader =
        AssetsUtils.getAssetsFileContent(EasyApplication.application, "shader/matrix_vertex_shader")
    private val fragmentShader = AssetsUtils.getAssetsFileContent(
        EasyApplication.application,
        "shader/simple_fragment_shader"
    )
    private var program: Int = 0
    private var hasInited = false
    private var width = 0
    private var height = 0
    private var matrix: FloatArray = FloatArray(16)
    private var positionAttr: Int = 0
    private var matrixUniform: Int = 0
    private var colorUniform: Int = 0
    private val radius: Float
    private val points = mutableListOf<Float>()
    private var pointsBuffer: FloatBuffer? = null
    private var color = floatArrayOf(1f, 1f, 1f, 1f)

    fun setMatrix(matrix: FloatArray) {
        this.matrix = matrix
    }

    companion object {
        const val DEFAULT_RADIUS = 2f
        const val POINTS_COUNT = 180
    }


    constructor() : this(DEFAULT_RADIUS)
    constructor(radius: Float) {
        this.radius = radius
    }


    override fun ifNeedInit(width: Int, height: Int) {
        if (!hasInited) {
            hasInited = true
            program = ShaderUtil.createShaderProgram(vertexShader, fragmentShader)
            positionAttr = GLES20.glGetAttribLocation(program, "vPosition")
            matrixUniform = GLES20.glGetUniformLocation(program, "vMatrix")
            colorUniform = GLES20.glGetUniformLocation(program, "vColor")
            assembleList()
        }
        if (this.width != width || this.height != height) {
            this.width = width
            this.height = height
            var projectionMatrix = FloatArray(16)
            var viewMatrix = FloatArray(16)
            Matrix.perspectiveM(projectionMatrix, 0, 60f, width * 1f / height, 1f, 20f)
            Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 10f, 0f, 0f, 0f, 0f, 1f, 0f)
            Matrix.multiplyMM(matrix, 0, projectionMatrix, 0, viewMatrix, 0)
        }

    }

    private fun assembleList() {
        val step = 360 / POINTS_COUNT
        var corner = 0
        points.add(0f)
        points.add(0f)
        while (corner < 360 + step) {
            val x = radius * cos(corner * Math.PI / 180)
            val y = radius * sin(corner * Math.PI / 180)
            points.add(x.toFloat())
            points.add(y.toFloat())
            corner += step
        }
        val byteBuffer = ByteBuffer.allocateDirect(points.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        pointsBuffer = byteBuffer.asFloatBuffer()
        pointsBuffer?.put(points.toFloatArray())
        pointsBuffer?.position(0)
    }

    override fun draw(gl: GL10?) {
        //GLES20.glClearColor(0f,0f,0f,1f)
        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(program)
        GLES20.glEnableVertexAttribArray(positionAttr)
        GLES20.glVertexAttribPointer(
            positionAttr,
            2,
            GLES20.GL_FLOAT,
            false,
            2 * 4,
            pointsBuffer
        )
        GLES20.glUniform4fv(colorUniform, 1, color, 0)
        GLES20.glUniformMatrix4fv(matrixUniform, 1, false, matrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, points.size / 2)
        GLES20.glDisableVertexAttribArray(positionAttr)
    }
}