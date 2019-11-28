package com.denny.easyopengl.render

import android.opengl.GLES20
import com.denny.easyopengl.painter.texture.ExternalTexturePainter
import com.denny.easyopengl.util.L
import javax.microedition.khronos.opengles.GL10

class FboRender : EasyRender() {
    val fbos = IntArray(1)
    val fboTextures = IntArray(1)
    private var width = 0
    private var height = 0
    private val texturePainter = ExternalTexturePainter()
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        this.width = width
        this.height = height
        createTexFbo()
    }

    override fun onDrawFrame(gl: GL10?) {
        //1.绘制painter到fbo中
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbos[0])
        super.onDrawFrame(gl)
        //2.绘制fbo texture到窗口上
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        texturePainter.textureId = fboTextures[0]
        texturePainter.ifNeedInit(width, height)
        texturePainter.draw(gl)
    }


    private fun createTexFbo() {
        //create frame buffer
        GLES20.glGenFramebuffers(1, fbos, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbos[0])

        //create texture attachment
        GLES20.glGenTextures(1, fboTextures, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextures[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
            width,
            height,
            0,
            GLES20.GL_RGBA,
            GLES20.GL_INT,
            null
        )
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D,
            fboTextures[0],
            0
        )
        val isFboComplete =
            GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) == GLES20.GL_FRAMEBUFFER_COMPLETE
        L.e("fbo complete:$isFboComplete")
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)

    }
}