package com.denny.easyopengl.painter.texture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import com.denny.easyopengl.EasyApplication
import com.denny.easyopengl.painter.IPainter
import com.denny.easyopengl.util.AssetsUtils
import com.denny.easyopengl.util.ShaderUtil
import com.denny.easyopengl.util.tofloatBuffer
import java.nio.ByteBuffer
import javax.microedition.khronos.opengles.GL10

class TexturePainter : IPainter {
    private var vertexShader = AssetsUtils.getAssetsFileContent(
        EasyApplication.application,
        "shader/texture/texture_vertex_shader.vert"
    )
    private var fragmentShader = AssetsUtils.getAssetsFileContent(
        EasyApplication.application,
        "shader/texture/texture_fragment_shader.frag"
    )
    private var width = 0
    private var height = 0
    private var hasInited = false
    private var shaderProgram = 0
    private val matrix = FloatArray(16)
    private val vertexs = floatArrayOf(
        -1f, 1f,
        -1f, -1f,
        1f, 1f,
        1f, -1f
    )

    private val vertexBuff = ByteBuffer.allocateDirect(vertexs.size * 4).tofloatBuffer(vertexs)
    //    private val textureVertexs = floatArrayOf(
//        0.0f, 0.0f,
//        0.0f, 1.0f,
//        1.0f, 0.0f,
//        1.0f, 1.0f
//    )
    //android坐标和opengl坐标系统不一样;加载bitmap的时候需要垂直翻转一下
    private val textureVertexs = floatArrayOf(
        0.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        1.0f, 0f
    )
    private val textureVertexBuff =
        ByteBuffer.allocateDirect(textureVertexs.size * 4).tofloatBuffer(textureVertexs)

    private var defaultBitmap: Bitmap = loadDefaultBitmap()


    override fun ifNeedInit(width: Int, height: Int) {
        if (!hasInited) {
            hasInited = true
            shaderProgram = ShaderUtil.createShaderProgram(vertexShader, fragmentShader)
        }
        if (this.width != width || this.height != height) {
            this.width = width
            this.height = height
//            val projectionMatrix = FloatArray(16)
//            Matrix.perspectiveM(projectionMatrix, 0, 60f, width * 1f / height, 1f, 20f)
//            val viewMatrix = FloatArray(16)
//            Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1f, 0f)
//            Matrix.multiplyMM(matrix, 0, projectionMatrix, 0, viewMatrix, 0)
            if (width > height) {
                Matrix.orthoM(
                    matrix,
                    0,
                    -width * 1f / height,
                    width * 1f / height,
                    -1f,
                    1f,
                    -1f,
                    1f
                )

            } else {
                Matrix.orthoM(
                    matrix,
                    0,
                    -1f,
                    1f,
                    -height * 1f / width,
                    height * 1f / width,
                    -1f,
                    1f
                )
            }

        }
    }

    private fun loadTexture() {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, defaultBitmap, 0)
    }

    private fun loadDefaultBitmap(): Bitmap {
        val byteData = AssetsUtils.getAssetsFileData(
            EasyApplication.application,
            "image/saber2.jpeg"
        )
        val oriBitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.size)
        val matrix = android.graphics.Matrix()
        matrix.postScale(1f, -1f)
        return Bitmap.createBitmap(
            oriBitmap,
            0,
            0,
            oriBitmap.width,
            oriBitmap.height,
            matrix,
            false
        )
    }

    override fun draw(gl: GL10?) {
        GLES20.glUseProgram(shaderProgram)
        GLES20.glClearColor(1f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUniformMatrix4fv(
            GLES20.glGetUniformLocation(shaderProgram, "vMatrix"),
            1,
            false,
            matrix,
            0
        )
        var positionAttr = GLES20.glGetAttribLocation(shaderProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(positionAttr)
        GLES20.glVertexAttribPointer(
            positionAttr,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuff
        )
        var texturePositionAttr = GLES20.glGetAttribLocation(shaderProgram, "vTexturePos")
        GLES20.glEnableVertexAttribArray(texturePositionAttr)
        GLES20.glVertexAttribPointer(
            texturePositionAttr,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            textureVertexBuff
        )
        GLES20.glUniform1i(GLES20.glGetUniformLocation(shaderProgram, "vTexture"), 0)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(shaderProgram,"isHalf"),1)
        loadTexture()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexs.size / 2)
        GLES20.glDisableVertexAttribArray(positionAttr)
        GLES20.glDisableVertexAttribArray(texturePositionAttr)
    }
}