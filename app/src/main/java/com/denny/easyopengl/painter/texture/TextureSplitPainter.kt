package com.denny.easyopengl.painter.texture

import android.opengl.GLES20
import com.denny.easyopengl.EasyApplication
import com.denny.easyopengl.util.AssetsUtils
import javax.microedition.khronos.opengles.GL10

class TextureSplitPainter : TexturePainter() {
    override var fragmentShader = AssetsUtils.getAssetsFileContent(
        EasyApplication.application,
        "shader/texture/texture_split_screen.frag"
    )

    override fun subDraw(gl: GL10?, program: Int) {
        super.subDraw(gl, program)
        GLES20.glUniform1f(GLES20.glGetUniformLocation(program, "horizontalCount"), 2.5f)
        GLES20.glUniform1f(GLES20.glGetUniformLocation(program, "verticalCount"), 2f)
    }

}