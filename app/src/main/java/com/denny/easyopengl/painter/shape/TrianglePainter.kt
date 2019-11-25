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

class TrianglePainter : IPainter {
    private var hasInited = false
    private val vertexShaderCode: String =
        AssetsUtils.getAssetsFileContent(EasyApplication.application, "shader/matrix_vertex_shader")
    private val fragmentShaderCode: String =
        AssetsUtils.getAssetsFileContent(
            EasyApplication.application,
            "shader/simple_fragment_shader"
        )
    private var program: Int = 0
    private var positionAttr: Int = 0
    private var colorUniform: Int = 0
    private var matrixUniform: Int = 0
    private val triangleCoords =
        floatArrayOf(
            -0.5f, 0f, 0f,
            0.5f, 0f, 0f,
            0f, 0.5f, 0f
        )
    private val color = floatArrayOf(1f, 0f, 0f, 1f)
    private lateinit var triangleBuffer: FloatBuffer
    private val COORDIS_PER_VERTEX = 3
    private val VERTEX_STRIDE = COORDIS_PER_VERTEX * 4
    private val VERTEX_COUNT = triangleCoords.size / COORDIS_PER_VERTEX
    private var width = 0
    private var height = 0
    private var matrix = FloatArray(16)


    override fun ifNeedInit(width: Int, height: Int) {
        if (!hasInited) {
            hasInited = true
            program = ShaderUtil.createShaderProgram(vertexShaderCode, fragmentShaderCode)
            positionAttr = GLES20.glGetAttribLocation(program, "vPosition")
            colorUniform = GLES20.glGetUniformLocation(program, "vColor")
            matrixUniform = GLES20.glGetUniformLocation(program, "vMatrix")
            val buffer = ByteBuffer.allocateDirect(triangleCoords.size * 4)
            buffer.order(ByteOrder.nativeOrder())
            triangleBuffer = buffer.asFloatBuffer()
            triangleBuffer.put(triangleCoords)
            triangleBuffer.position(0)

        }
        if (this.width != width || this.height != height) {
            this.width = width
            this.height = height
            val aspect = if (width > height) width * 1.0f / height else height * 1.0f / width
            if (width > height) {
                Matrix.orthoM(matrix, 0, -aspect, aspect, -1f, 1f, -1f, 1f)
            } else {
                Matrix.orthoM(matrix, 0, -1f, 1f, -aspect, aspect, -1f, 1f)
            }
        }
    }

    override fun draw(gl: GL10?) {
        GLES20.glUseProgram(program)
        GLES20.glClearColor(0f, 1f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glVertexAttribPointer(
            positionAttr,
            COORDIS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            VERTEX_STRIDE,
            triangleBuffer
        )
        GLES20.glUniform4fv(colorUniform, 1, color, 0)
        GLES20.glUniformMatrix4fv(matrixUniform,1,false,matrix,0)
        GLES20.glEnableVertexAttribArray(positionAttr)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_COUNT)
        GLES20.glDisableVertexAttribArray(positionAttr)
    }
}