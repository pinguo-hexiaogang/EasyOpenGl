package com.denny.easyopengl.util

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


fun ByteBuffer.toFloatBuffer(floatArray: FloatArray): FloatBuffer {
    order(ByteOrder.nativeOrder())
    val floatBuffer = asFloatBuffer()
    floatBuffer.put(floatArray)
    floatBuffer.position(0)
    return floatBuffer
}

fun FloatArray.toFloatBuffer(): FloatBuffer {
    return ByteBuffer.allocateDirect(this.size * 4).toFloatBuffer(this)
}

fun ByteArray.toByteBuffer(): ByteBuffer {
    val buffer = ByteBuffer.allocateDirect(this.size)
    buffer.order(ByteOrder.nativeOrder())
    buffer.put(this)
    buffer.position(0)
    return buffer
}

fun InputStream?.readBytesCheck(bytes: ByteArray): Boolean {
    if(this == null){
        return false
    }
    return read(bytes) >= bytes.size
}