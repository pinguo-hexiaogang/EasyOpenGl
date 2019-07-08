package com.denny.easyopengl.util

import android.content.Context
import android.content.res.AssetManager
import android.text.TextUtils
import java.io.*
import java.nio.charset.Charset

/**
 * A collection for asset's action
 * Created by yingyu on 2/12/14.
 * Refactor to kotlin by shijiangjun on 2018/4/25
 */
object AssetsUtils {
    private const val BUFFER_SIZE = 8 * 1024

    /**
     * 拷贝文件到/data/data/packageName/files/filename
     *
     * @param context
     * @param assetsFileName
     * @param filename       支持aaa或者aaaa/bbb格式
     * @throws IOException
     * @author liubo
     */
    @Throws(IOException::class)
    fun copyAssetsToDataFiles(context: Context, assetsFileName: String, filename: String) {
        val am = context.assets
        var `in`: BufferedInputStream? = null
        val haveFolder = filename.indexOf('/') != -1
        var file: File? = null
        if (haveFolder) {
            file = File(context.filesDir.absolutePath + File.separator + filename)
            val parent = file.parentFile
            if (!FileUtils.checkFolder(parent)) {
                throw IOException("Create Folder(" + parent.absolutePath + ") Failed!")
            }
        }

        try {
            L.i("Copy files from:$assetsFileName to:$filename")
            `in` = BufferedInputStream(am.open(assetsFileName, AssetManager.ACCESS_BUFFER))
            var out: BufferedOutputStream? = null
            try {
                if (haveFolder) {
                    out = BufferedOutputStream(FileOutputStream(file!!))
                } else {
                    out = BufferedOutputStream(context.openFileOutput(filename, Context.MODE_PRIVATE))
                }
                val buffer = ByteArray(BUFFER_SIZE)
                var len = `in`.read(buffer)
                while (len != -1) {
                    out.write(buffer, 0, len)
                    len = `in`.read(buffer)
                }
                out.flush()
            } finally {
                FileUtils.close(out)
            }
        } finally {
            FileUtils.close(`in`)
        }
    }

    fun hasAssetsFile(context: Context, dir: String, findFile: String): Boolean {
        if (TextUtils.isEmpty(findFile)) {
            return false
        }
        val am = context.assets
        try {
            val files = am.list(dir)
            for (file in files) {
                if (findFile == file) {
                    return true
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return false
    }

    @Throws(IOException::class)
    fun copyAssetsFileTo(context: Context, assetsFileName: String, descFile: File) {
        L.i("copy from asserts: " + assetsFileName + " to: " + descFile.absolutePath)
        val am = context.assets
        var `in`: BufferedInputStream? = null

        val parent = descFile.parentFile
        if (null != parent) {
            if (!FileUtils.checkFolder(parent)) {
                throw IOException("Create Folder(" + parent.absolutePath + ") Failed!")
            }
        }

        try {
            `in` = BufferedInputStream(am.open(assetsFileName, AssetManager.ACCESS_BUFFER))
            var out: BufferedOutputStream? = null
            try {
                out = BufferedOutputStream(FileOutputStream(descFile))

                val buffer = ByteArray(BUFFER_SIZE)
                var len = `in`.read(buffer)
                while (len != -1) {
                    out.write(buffer, 0, len)
                    len = `in`.read(buffer)
                }
                out.flush()
            } finally {
                FileUtils.close(out)
            }
        } catch (e: IOException) {
            throw e
        } finally {
            FileUtils.close(`in`)
        }
    }

    /**
     * 取得Assert的文件内容
     *
     * @param context
     * @param fileName
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getAssetsFileContent(context: Context, fileName: String): String {
        val bytes = getAssetsFileData(context, fileName)
        return String(bytes, Charset.defaultCharset())
    }

    /**
     * 取得Assets的文件内容，针对中文等双字节码，分批次读取会产生中文乱码
     *
     * @param context
     * @param fileName
     * @return
     */
    fun getAssetsFileContentForChinese(context: Context, fileName: String): String {
        var content = ""
        try {
            val sb = StringBuffer()
            val `is` = context.assets.open(fileName)
            var len: Int
            val buf = ByteArray(`is`.available())
            len = `is`.read(buf)
            while (len != -1) {
                sb.append(String(buf, 0, len, Charset.defaultCharset()))
                len = `is`.read(buf)
            }
            `is`.close()

            content = sb.toString()
        } catch (e: IOException) {
        }

        return content
    }

    /**
     * @param fileName 文件名
     * @return byte[]
     * @Description 从assets文件目录下获取文件数据.
     * @author liubo
     */
    @Throws(IOException::class)
    private fun getAssetsFileData(context: Context, fileName: String): ByteArray {
        val am = context.assets
        var `in`: BufferedInputStream? = null
        try {
            `in` = BufferedInputStream(am.open(fileName, AssetManager.ACCESS_BUFFER))
            return FileUtils.getStreamData(`in`)
        } finally {
            FileUtils.close(`in`)
        }
    }
}
