package com.denny.easyopengl.util

import android.opengl.GLES20
import java.lang.IllegalArgumentException

object ShaderUtil {
    fun loadShader(type: Int, shaderCode: String): Int {
        //根据type创建顶点着色器或者片元着色器
        val shader = GLES20.glCreateShader(type)
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        checkCompileStatus(shader, shaderCode)
        return shader
    }

    private fun checkCompileStatus(shader: Int, shaderCode: String): Boolean {
        val compileArray = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileArray, 0)
        if (compileArray[0] == GLES20.GL_FALSE) {
            L.e("shader compile fail\n$shaderCode")
        } else {
            L.e("shader compile success")
        }
        val logLengthArray = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_INFO_LOG_LENGTH, logLengthArray, 0)
        if (logLengthArray[0] >= 1) {
            val logInfo = GLES20.glGetShaderInfoLog(shader)
            L.e("shader error msg:\n$logInfo")
        }
        return compileArray[0] == GLES20.GL_TRUE
    }

    fun createShaderProgram(vertexShaderCode: String, fragmentShaderCode: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        val program = GLES20.glCreateProgram()
        //将顶点着色器加入到程序
        GLES20.glAttachShader(program, vertexShader)
        //将片元着色器加入到程序中
        GLES20.glAttachShader(program, fragmentShader)
        //连接到着色器程序
        GLES20.glLinkProgram(program)
        if (GLES20.glGetError() != GLES20.GL_NO_ERROR) {
            throw IllegalArgumentException("create shader fail")
        }
        return program
    }

}