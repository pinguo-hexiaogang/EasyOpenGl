package com.denny.easyopengl.painter.texture

import android.opengl.GLES20
import com.denny.easyopengl.EasyApplication
import com.denny.easyopengl.util.AssetsUtils

/**
 * 使用外部传过来的纹理，而不是默认加载的bitmap
 */
class ExternalTexturePainter : TexturePainter() {
    var textureId = 0
    override var fragmentShader = AssetsUtils.getAssetsFileContent(
        EasyApplication.application,
        "shader/texture/external_texture_fragment_shader.frag"
    )

    override fun loadTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
    }
}