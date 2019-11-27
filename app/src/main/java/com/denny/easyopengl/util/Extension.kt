package com.denny.easyopengl.util

import java.io.InputStream
import java.nio.*


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

fun IntArray.toIntBuffer(): IntBuffer {
    val buff = ByteBuffer.allocateDirect(size * 4)
    buff.order(ByteOrder.nativeOrder())
    val intBuff = buff.asIntBuffer()
    intBuff.put(this).position(0)
    return intBuff
}

fun ShortArray.toShortBuffer(): ShortBuffer{
    val buff = ByteBuffer.allocateDirect(size * 2)
    buff.order(ByteOrder.nativeOrder())
    val shortBuff = buff.asShortBuffer()
    shortBuff.put(this).position(0)
    return shortBuff
}

fun InputStream?.readBytesCheck(bytes: ByteArray): Boolean {
    if (this == null) {
        return false
    }
    return read(bytes) >= bytes.size
}