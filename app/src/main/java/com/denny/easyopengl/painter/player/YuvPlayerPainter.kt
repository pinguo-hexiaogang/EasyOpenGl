package com.denny.easyopengl.painter.player

import android.opengl.GLES20
import android.opengl.Matrix
import com.denny.easyopengl.EasyApplication
import com.denny.easyopengl.painter.IPainter
import com.denny.easyopengl.util.*
import java.io.File
import java.io.InputStream
import javax.microedition.khronos.opengles.GL10

class YuvPlayerPainter : IPainter {
    companion object {
        const val VIDEO_PATH = "/sdcard/testVideo/beayty.yuv"
        const val VIDEO_WIDTH = 960
        const val VIDEO_HEIGHT = 1280
    }

    private val vertexShader = AssetsUtils.getAssetsFileContent(
        EasyApplication.application,
        "shader/player/yuv_player_vertex.vert"
    )
    private val fragmentShader = AssetsUtils.getAssetsFileContent(
        EasyApplication.application,
        "shader/player/yuv_player_frag.frag"
    )
    private var program = 0

    private var hasInit = false

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
    private var width = 0
    private var height = 0
    private var matrix = FloatArray(16)
    private var inputStream: InputStream? = null
    private lateinit var textures: IntArray

    override fun ifNeedInit(width: Int, height: Int) {
        if (!hasInit) {
            inputStream = File(VIDEO_PATH).inputStream()
            hasInit = true
            program = ShaderUtil.createShaderProgram(vertexShader, fragmentShader)
            GLES20.glUseProgram(program)
            textures = createTextures()
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

    private fun createTextures(): IntArray {
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "yTexture"), 0)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "uTexture"), 1)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "vTexture"), 2)
        val textures = IntArray(3)
        GLES20.glGenTextures(3, textures, 0)
        textures.forEachIndexed { index, texture ->
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)
            if (index == 0) {
                GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, VIDEO_WIDTH,
                    VIDEO_HEIGHT, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, null
                )
            } else {
                GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, VIDEO_WIDTH / 2,
                    VIDEO_HEIGHT / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, null
                )
            }
        }
        return textures
    }

    override fun draw(gl: GL10?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(program)
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
        readYuvFrames()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(aPosition)
        GLES20.glDisableVertexAttribArray(aTexturePos)
    }

    private fun readYuvFrames() {
        val yBuffer = ByteArray(VIDEO_WIDTH * VIDEO_HEIGHT)
        val uBuffer = ByteArray(VIDEO_WIDTH * VIDEO_HEIGHT / 4)
        val vBuffer = ByteArray(VIDEO_WIDTH * VIDEO_HEIGHT / 4)
        if (inputStream.readBytesCheck(yBuffer)
            && inputStream.readBytesCheck(uBuffer)
            && inputStream.readBytesCheck(vBuffer)
        ) {
        } else {
            inputStream?.close()
            inputStream = File(VIDEO_PATH).inputStream()
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])
        GLES20.glTexSubImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            0,
            0,
            VIDEO_WIDTH,
            VIDEO_HEIGHT,
            GLES20.GL_LUMINANCE,
            GLES20.GL_UNSIGNED_BYTE,
            yBuffer.toByteBuffer()
        )
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1])
        GLES20.glTexSubImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            0,
            0,
            VIDEO_WIDTH / 2,
            VIDEO_HEIGHT / 2,
            GLES20.GL_LUMINANCE,
            GLES20.GL_UNSIGNED_BYTE,
            uBuffer.toByteBuffer()
        )
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[2])
        GLES20.glTexSubImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            0,
            0,
            VIDEO_WIDTH / 2,
            VIDEO_HEIGHT / 2,
            GLES20.GL_LUMINANCE,
            GLES20.GL_UNSIGNED_BYTE,
            vBuffer.toByteBuffer()
        )

    }
}