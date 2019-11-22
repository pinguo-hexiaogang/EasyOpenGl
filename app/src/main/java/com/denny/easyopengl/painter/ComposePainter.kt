package com.denny.easyopengl.painter

import java.util.concurrent.CopyOnWriteArrayList
import javax.microedition.khronos.opengles.GL10

class ComposePainter : IPainter {
    private val painters = CopyOnWriteArrayList<IPainter>()
    fun addPinters(painters: List<IPainter>) {
        this.painters.addAll(painters)
    }

    fun removePainter(painter: IPainter) {
        painters.remove(painter)
    }

    fun clearPainters() {
        painters.clear()
    }


    override fun ifNeedInit(width: Int, height: Int) {
        painters.forEach {
            it.ifNeedInit(width,height)
        }
    }

    override fun draw(gl: GL10?) {
        painters.forEach {
            it.draw(gl)
        }
    }
}