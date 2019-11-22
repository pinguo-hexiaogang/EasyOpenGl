package com.denny.easyopengl.util

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


fun ByteBuffer.tofloatBuffer(floatArray: FloatArray): FloatBuffer {
    order(ByteOrder.nativeOrder())
    val floatBuffer = asFloatBuffer()
    floatBuffer.put(floatArray)
    floatBuffer.position(0)
    return floatBuffer
}