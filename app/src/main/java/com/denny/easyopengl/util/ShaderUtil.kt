package com.denny.easyopengl.util

import android.opengl.GLES20

object ShaderUtil {
    fun loadShader(type: Int, shaderCode: String): Int {
        //根据type创建顶点着色器或者片元着色器
        val shader = GLES20.glCreateShader(type)
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
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
        return program
    }

}