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

class SquarePainter : IPainter {
    private var hasInited = false
    private val vertexShaderCode: String =
        AssetsUtils.getAssetsFileContent(EasyApplication.application, "shader/matrix_vertex_shader")
    private val fragmentShaderCode: String =
        AssetsUtils.getAssetsFileContent(EasyApplication.application, "shader/simple_fragment_shader")
    private var program: Int = 0
    private var positionAttr: Int = 0
    private var colorUniform: Int = 0
    private val triangleCoords =
        floatArrayOf(
            -0.5f, 0.5f,
            -0.5f, -0.5f,
            0.5f, 0.5f,
            0.5f, -0.5f
        )
    private val color = floatArrayOf(1f, 0f, 0f, 1f)
    private lateinit var triangleBuffer: FloatBuffer
    private var width: Int = 0
    private var height: Int = 0

    private var matrixUniform: Int = 0
    private var projectMatrix = FloatArray(16)
    private var modelMatrix = FloatArray(16)
    private var mpMatrix = FloatArray(16)


    override fun ifNeedInit(width: Int, height: Int) {
        if (!hasInited) {
            hasInited = true
            program = ShaderUtil.createShaderProgram(vertexShaderCode, fragmentShaderCode)
            positionAttr = GLES20.glGetAttribLocation(program, "vPosition")
            colorUniform = GLES20.glGetUniformLocation(program, "vColor")
            val buffer = ByteBuffer.allocateDirect(triangleCoords.size * 4)
            buffer.order(ByteOrder.nativeOrder())
            triangleBuffer = buffer.asFloatBuffer()
            triangleBuffer.put(triangleCoords)
            triangleBuffer.position(0)
            matrixUniform = GLES20.glGetUniformLocation(program, "vMatrix")

        }
        if (this.width != width || this.height != height) {
            this.width = width
            this.height = height
            val projectionMatrix = FloatArray(16)
            Matrix.perspectiveM(projectionMatrix, 0, 60f, width * 1f / height, 1f, 20f)
            val viewMatrix = FloatArray(16)
            Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1f, 0f)
            Matrix.multiplyMM(mpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        }
    }

    override fun draw(gl: GL10?) {
        GLES20.glUseProgram(program)
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glVertexAttribPointer(
            positionAttr,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            triangleBuffer
        )
        GLES20.glUniform4fv(colorUniform, 1, color, 0)
        GLES20.glUniformMatrix4fv(matrixUniform, 1, false, mpMatrix, 0)
        GLES20.glEnableVertexAttribArray(positionAttr)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(positionAttr)
    }
}