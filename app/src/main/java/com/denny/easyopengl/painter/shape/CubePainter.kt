package com.denny.easyopengl.painter.shape

import android.opengl.GLES20
import android.opengl.Matrix
import com.denny.easyopengl.EasyApplication
import com.denny.easyopengl.painter.IPainter
import com.denny.easyopengl.util.AssetsUtils
import com.denny.easyopengl.util.ShaderUtil
import com.denny.easyopengl.util.toFloatBuffer
import com.denny.easyopengl.util.toShortBuffer
import javax.microedition.khronos.opengles.GL10

class CubePainter : IPainter {
    private val vertexsBuf = floatArrayOf(
        -1f, 1f, 1f,//正面左上
        -1f, -1f, 1f,//正面左下
        1f, 1f, 1f,//正面右上
        1f, -1f, 1f,//正面右下

        -1f, 1f, -1f,//反面左上
        -1f, -1f, -1f,//反面左下
        1f, 1f, -1f,//反面右上
        1f, -1f, -1f//反面右下
    ).toFloatBuffer()

    private val colors = floatArrayOf(
        1f, 0f, 0f,
        0f, 1f, 0f,
        0f, 0f, 1f,
        0.5f, 0.5f, 0.5f,

        0.2f, 1f, 1f,
        0.3f, 0f, 0f,
        1f, 1f, 1f,
        0.2f, 1f, 0f

    ).toFloatBuffer()

    private val indexs = shortArrayOf(
        0, 1, 2, 2, 1, 3,//正面
        4, 5, 6, 6, 5, 7,//反面
        4, 0, 6, 6, 0, 2,//上面
        5, 1, 7, 7, 1, 3,//下面
        4, 5, 1, 4, 1, 0,//左面
        6, 2, 7, 7, 2, 3//右面
    )

    private val vertexShader =
        AssetsUtils.getAssetsFileContent(EasyApplication.application, "shader/cube.vert")

    private val fragShader =
        AssetsUtils.getAssetsFileContent(EasyApplication.application, "shader/cube.frag")
    private var hasInit = false
    private var program = 0
    private var height = 0
    private var width = 0
    private var matrix = FloatArray(16)

    override fun ifNeedInit(width: Int, height: Int) {
        if (!hasInit) {
            hasInit = true
            program = ShaderUtil.createShaderProgram(vertexShader, fragShader)
        }
        if (this.width != width || this.height != height) {
            val viewMatrix = FloatArray(16)
            Matrix.setLookAtM(viewMatrix, 0, 5f, 5f, 10f, 0f, 0f, 0f, 0f, 1f, 0f)
            val projectionMatrix = FloatArray(16)
            Matrix.perspectiveM(projectionMatrix, 0, 30f, width * 1f / height, 1f, 20f)
            Matrix.multiplyMM(matrix, 0, projectionMatrix, 0, viewMatrix, 0)
        }
    }

    override fun draw(gl: GL10?) {
        GLES20.glUseProgram(program)
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        val aPosition = GLES20.glGetAttribLocation(program, "aPosition")
        GLES20.glEnableVertexAttribArray(aPosition)
        GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 0, vertexsBuf)
        GLES20.glUniformMatrix4fv(
            GLES20.glGetUniformLocation(program, "uMatrix"),
            1,
            false,
            matrix,
            0
        )

        val aColor = GLES20.glGetAttribLocation(program, "aColor")
        GLES20.glEnableVertexAttribArray(aColor)
        GLES20.glVertexAttribPointer(aColor, 3, GLES20.GL_FLOAT, false, 0, colors)
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indexs.size,
            GLES20.GL_UNSIGNED_SHORT,
            indexs.toShortBuffer()
        )
        GLES20.glDisableVertexAttribArray(aPosition)
        GLES20.glDisableVertexAttribArray(aColor)
    }
}