package com.denny.easyopengl.painter.player

import android.opengl.GLES20
import android.opengl.Matrix
import com.denny.easyopengl.EasyApplication
import com.denny.easyopengl.painter.IPainter
import com.denny.easyopengl.util.AssetsUtils
import com.denny.easyopengl.util.ShaderUtil
import com.denny.easyopengl.util.toFloatBuffer
import com.denny.easyopengl.util.toIntBuffer
import javax.microedition.khronos.opengles.GL10

class YuvPlayerMotionPainter : IPainter {
    private var vertexShader = AssetsUtils.getAssetsFileContent(
        EasyApplication.application,
        "shader/player/yuv_player_motion.vert"
    )
    private var fragmentShader = AssetsUtils.getAssetsFileContent(
        EasyApplication.application,
        "shader/player/yuv_player_motion.frag"
    )
    private var hasInit: Boolean = false
    private var program = 0
    private var width = 0
    private var height = 0
    private val matrix = FloatArray(16)
    private var vertexsBuf = floatArrayOf(
        -1f, 1f,
        -1f, -1f,
        1f, 1f,
        1f, -1f
    ).toFloatBuffer()

    private var textureCorsBuf = floatArrayOf(
        0f, 1f,
        0f, 0f,
        1f, 1f,
        1f, 0f
    ).toFloatBuffer()

    private var curUnit = 0

    private var pixels =
        IntArray(YuvPlayerPainter.VIDEO_WIDTH * YuvPlayerPainter.VIDEO_HEIGHT).toIntBuffer()

    private fun updateCurUnit() {
        curUnit = (curUnit + 1) % 6
    }

    private lateinit var textures: IntArray

    override fun ifNeedInit(width: Int, height: Int) {
        if (!hasInit) {
            hasInit = true
            program = ShaderUtil.createShaderProgram(vertexShader, fragmentShader)
            GLES20.glUseProgram(program)
            textures = loadTextures()
        }
        if (this.width != width || this.height != height) {
            this.width = width
            this.height = height
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

    private fun loadTextures(): IntArray {
        val textures = IntArray(6)
        GLES20.glGenTextures(6, textures, 0)
        textures.forEachIndexed { index, texture ->
            GLES20.glActiveTexture(index)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                YuvPlayerPainter.VIDEO_WIDTH,
                YuvPlayerPainter.VIDEO_HEIGHT,
                0, GLES20.GL_RGBA, GLES20.GL_INT, null
            )
        }
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "texture0"), 0)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "texture1"), 1)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "texture2"), 2)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "texture3"), 3)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "texture4"), 4)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "texture5"), 5)
        return textures
    }

    override fun draw(gl: GL10?) {
        GLES20.glClearColor(1f,0f,0f,1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(program)
        GLES20.glReadPixels(
            0,
            0,
            YuvPlayerPainter.VIDEO_WIDTH,
            YuvPlayerPainter.VIDEO_HEIGHT,
            GLES20.GL_RGBA,
            GLES20.GL_INT,
            pixels
        )
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + curUnit)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[curUnit])
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
            YuvPlayerPainter.VIDEO_WIDTH,
            YuvPlayerPainter.VIDEO_HEIGHT,
            0,
            GLES20.GL_RGBA,
            GLES20.GL_INT,
            pixels
        )
        val aPosition = GLES20.glGetAttribLocation(program, "aPosition")
        GLES20.glEnableVertexAttribArray(aPosition)
        GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_FLOAT, false, 0, vertexsBuf)
        val aTexturePos = GLES20.glGetAttribLocation(program, "aTexturePos")
        GLES20.glEnableVertexAttribArray(aTexturePos)
        GLES20.glVertexAttribPointer(aTexturePos, 2, GLES20.GL_FLOAT, false, 0, textureCorsBuf)
        GLES20.glUniformMatrix4fv(
            GLES20.glGetUniformLocation(program, "uMatrix"),
            1,
            false,
            matrix,
            0
        )
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(aPosition)
        GLES20.glDisableVertexAttribArray(aTexturePos)
        updateCurUnit()
    }
}