package com.denny.easyopengl.util

import android.graphics.Bitmap
import android.text.TextUtils
import java.io.*
import java.math.BigDecimal
import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * Refactor to kotlin by shijiangjun on 2018/4/25.
 */
object FileUtils {
    private const val BUFFER_SIZE = 8 * 1024

    private const val SIZE_UNIT = 1024
    private const val SUFFIX_MB = "MB"
    private const val SUFFIX_GB = "GB"

    private val IGNORE_LOST_DIR = "LOST.DIR"
    private val IGNORE_NO_MEDIA = ".nomedia"

    fun isAccessAble(path: String): Boolean {
        val file = File(path)
        return isAccessAble(file)
    }

    fun isAccessAble(file: File?): Boolean {
        return if (null == file || !file.exists()) {
            false
        } else {
            file.canRead()
        }
    }

    fun isExist(path: String): Boolean {
        return File(path).exists()
    }

    fun isNoMedia(file: File): Boolean {
        val fName = file.name
        return !file.isDirectory && IGNORE_NO_MEDIA.equals(fName, ignoreCase = true)
    }

    fun isInValid(file: File): Boolean {
        val fName = file.name
        if (fName.startsWith(".")) {
            return true
        } else if (file.isDirectory && IGNORE_LOST_DIR.equals(fName, ignoreCase = true)) {
            return true
        }
        return false
    }

    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    fun copyToFile(inputStream: InputStream, destFile: File): Boolean {
        try {
            if (destFile.exists()) {
                if (!destFile.delete()) {
                    L.w("Delete file failed!")
                }
            }
            val out = FileOutputStream(destFile)
            try {
                val buffer = ByteArray(4096)
                var bytesRead = inputStream.read(buffer)
                while (bytesRead >= 0) {
                    out.write(buffer, 0, bytesRead)
                    bytesRead = inputStream.read(buffer)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                out.flush()
                try {
                    out.fd.sync()
                } catch (e: IOException) {
                }

                out.close()
            }
            return true
        } catch (e: IOException) {
            return false
        }

    }

    /**
     * 拷贝单个文件
     *
     * @param srcPath  源文件
     * @param destPath 目标文件
     */
    @Throws(IOException::class)
    fun copySingleFile(srcPath: String?, destPath: String?) {
        if (srcPath == null || destPath == null) {
            throw IOException("path is Null, srcPath=$srcPath,destPath=$destPath")
        }
        copySingleFile(File(srcPath), File(destPath))
    }

    /**
     * 拷贝单个文件（不管目标文件是否存在，都会创建一个空的文件）
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @author liubo
     */
    @Throws(IOException::class)
    fun copySingleFile(srcFile: File, destFile: File) {
        val parent = destFile.parentFile
        if (!checkFolder(parent)) {
            throw IOException("Create Folder(" + parent.absolutePath + ") Failed!")
        }

        var `in`: BufferedInputStream? = null
        try {
            `in` = BufferedInputStream(FileInputStream(srcFile))
            var out: BufferedOutputStream? = null
            try {
                // 不管目标文件是否存在，都会创建一个空的文件！
                out = BufferedOutputStream(FileOutputStream(destFile))
                val buffer = ByteArray(BUFFER_SIZE)
                var len = `in`.read(buffer)
                while (len != -1) {
                    out.write(buffer, 0, len)
                    len = `in`.read(buffer)
                }
                out.flush()
            } finally {
                close(out)
            }
        } finally {
            close(`in`)
        }
    }

    @Throws(IOException::class)
    fun copyFolder(src: File, dest: File) {
        L.d("Copy from: " + src.absolutePath + " to: " + dest.absolutePath)
        if (src.isDirectory) {
            checkFolder(dest)

            val files = src.list()

            if (null == files || files.size == 0) {
                L.d("files is empty and can't do copy")
                return
            }

            for (file in files) {
                val srcFile = File(src, file)
                val destFile = File(dest, file)

                // 递归
                copyFolder(srcFile, destFile)
            }

        } else {
            L.d("Copy file from: " + src.absolutePath + " to: " + dest.absolutePath)
            copySingleFile(src, dest)
        }
    }

    /**
     * 将文件转换为byte[] 数据.
     *
     * @param filePath 文件路径
     * @return byte[] 文件数据
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getFileData(filePath: String): ByteArray {
        return getFileData(File(filePath))
    }

    /**
     * 将文件转换为byte[] 数据.
     *
     * @param file 文件
     * @return byte[] 文件数据
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getFileData(file: File): ByteArray {
        var `in`: BufferedInputStream? = null
        try {
            `in` = BufferedInputStream(FileInputStream(file))
            return getStreamData(`in`)
        } finally {
            close(`in`)
        }
    }

    /**
     * 文件夹检查，不存在则新建
     *
     * @param folderPath 文件夹检查，不存在则新建
     * @return true，存在或新建成功，false，不存在或新建失败
     * @author liubo
     */
    fun checkFolder(folderPath: String?): Boolean {
        return if (folderPath == null) {
            false
        } else checkFolder(File(folderPath))
    }

    /**
     * 文件夹检查，不存在则新建
     *
     * @param folder 文件夹检查，不存在则新建
     * @return true，存在或新建成功，false，不存在或新建失败
     * @author liubo
     */
    fun checkFolder(folder: File?): Boolean {
        if (folder == null) {
            return false
        }

        return if (folder.isDirectory) {
            true
        } else folder.mkdirs()

    }

    /**
     * 取得文件内容
     *
     * @param file 文件
     * @return 文件
     * @throws Exception 读取异常
     */
    @Throws(IOException::class)
    fun getFileContent(file: File): String {
        var `in`: BufferedReader? = null
        var fileSize = file.length()
        if (fileSize > java.lang.Short.MAX_VALUE) {
            fileSize = java.lang.Short.MAX_VALUE.toLong()
        }
        val sb = StringBuilder(fileSize.toInt())
        try {
            `in` = BufferedReader(InputStreamReader(FileInputStream(file), "utf-8"))
            var line = `in`.readLine()
            while (line != null) {
                sb.append(line)
                sb.append('\n')
                line = `in`.readLine()
            }
        } finally {
            if (`in` != null) {
                `in`.close()
            }
        }

        return sb.toString()
    }

    /**
     * 将字符串写入文件
     *
     * @param file    写入的文件
     * @param content 文件内容
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    fun writeFileContent(file: File, content: String) {
        file.createNewFile()
        val out = FileOutputStream(file)
        try {
            out.write(content.toByteArray(Charset.defaultCharset()))
            out.flush()
        } finally {
            close(out)
        }
    }

    fun deleteFile(path: String?) {
        if (null == path || "" == path) {
            L.e("File path is null or not exist, delete file fail!")
            return
        }

        val file = File(path)
        deleteFile(file)
    }

    fun deleteFile(file: File?) {
        if (null == file || !file.exists()) {
            L.e("File is null or not exist, delete file fail!")
            return
        }

        if (file.isDirectory) {
            deleteFile(file.listFiles())
        }

        if (!file.delete()) {
            L.i("delete (" + file.path + ") failed!")
        }
    }

    fun deleteFile(file: File?, fileFilter: FileFilter) {
        if (null == file || !file.exists()) {
            L.e("File is null or not exist, delete file fail!")
            return
        }

        if (file.isDirectory) {
            deleteFile(file.listFiles(fileFilter))
        }

        if (fileFilter.accept(file) && !file.delete()) {
            L.i("delete (" + file.path + ") failed!")
        }
    }

    fun deleteFile(files: Array<File>?) {
        if (null == files || files.size == 0) {
            L.e("Files is null or empty, delete fail!")
            return
        }

        for (file in files) {
            deleteFile(file)
        }
    }

    fun close(closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    @Throws(IOException::class)
    fun getStreamData(`in`: InputStream): ByteArray {
        var out: ByteArrayOutputStream? = null
        try {
            out = ByteArrayOutputStream()
            val buffer = ByteArray(BUFFER_SIZE)
            var len = `in`.read(buffer)
            while (len != -1) {
                out.write(buffer, 0, len)
                len = `in`.read(buffer)
            }
            out.flush()
            return out.toByteArray()
        } finally {
            close(out)
        }
    }

    /**
     * 保存Bitmap
     */
    @Throws(IOException::class)
    fun saveBitmap(path: String, bitmap: Bitmap?, quality: Int) {
        if (TextUtils.isEmpty(path) || bitmap == null) {
            throw IOException("参数错误:path=$path /bitmap:$bitmap")
        }

        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(path)
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                out.flush()
            }
        } finally {
            close(out)
        }
    }

    /**
     * 保存Bitmap
     */
    @Throws(IOException::class)
    fun saveBitmap(path: String, bitmap: Bitmap?, quality: Int, format: Bitmap.CompressFormat) {
        if (TextUtils.isEmpty(path) || bitmap == null) {
            throw IOException("参数错误:path=$path /bitmap:$bitmap")
        }

        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(path)
            if (bitmap.compress(format, quality, out)) {
                out.flush()
            }
        } finally {
            close(out)
        }
    }

    /**
     * 保存文件
     *
     * @param data 二进制数据文件
     * @param path 路径
     * @throws IOException IOException
     * @author liubo
     */
    @Throws(IOException::class)
    fun saveFile(data: ByteArray?, path: String?) {
        if (data == null) {
            throw IOException("data is null")
        }

        if (path == null) {
            throw IOException("path is null")
        }

        val parent = File(path).parentFile
        if (!checkFolder(parent)) {
            throw IOException("Create Folder(" + parent.absolutePath + ") Failed!")
        }

        var out: BufferedOutputStream? = null
        try {
            out = BufferedOutputStream(FileOutputStream(path))
            out.write(data)
        } finally {
            close(out)
        }
    }

    /**
     * 返回文本文件行数
     */
    @Throws(IOException::class)
    fun getLineNumber(file: File): Int {
        var lnr: LineNumberReader? = null
        try {
            lnr = LineNumberReader(InputStreamReader(FileInputStream(file), Charset.defaultCharset()))
            lnr.skip(Integer.MIN_VALUE.toLong())
            return lnr.lineNumber
        } finally {
            close(lnr)
        }
    }

    /**
     * Copy from one stream to another.  Throws IOException in the event of error
     * (for example, SD card is full)
     *
     * @param is     Input stream.
     * @param os     Output stream.
     * @param buffer Temporary buffer to use for copy.
     */
    @Throws(IOException::class)
    @JvmOverloads
    fun copyStream(`is`: InputStream, os: OutputStream, buffer: ByteArray = ByteArray(BUFFER_SIZE)) {
        var count = `is`.read(buffer)
        while (count != -1) {
            os.write(buffer, 0, count)
            count = `is`.read(buffer)
        }
    }

    /**
     * 获取文件的后缀名字,比如123.jpg，返回jpg
     *
     * @param filePath 文件的路径
     * @return 文件的后缀名
     */
    fun getFileSuffix(filePath: String?): String? {
        var suffix: String? = null
        if (filePath != null) {
            val index = filePath.lastIndexOf(".")
            if (index != -1) {
                suffix = filePath.substring(index + 1)
            }
        }
        return suffix
    }

    /**
     * 获取文件的后缀名字,比如123.jpg，返回.jpg
     *
     * @param filePath 文件的路径
     * @return 文件的后缀名
     */
    fun getFileFullSuffix(filePath: String?): String? {
        var suffix: String? = null
        if (filePath != null) {
            val index = filePath.lastIndexOf(".")
            if (index != -1) {
                suffix = filePath.substring(index)
            }
        }
        return suffix
    }

    fun deleteDirectory(file: File) {
        if (file.exists()) {
            if (file.isDirectory) {
                val files = file.listFiles()
                for (file1 in files) {
                    if (file1.isDirectory) {
                        deleteDirectory(file1)
                    } else {
                        file1.delete()
                    }
                }
            }
            file.delete()
        }
    }

    /**
     * 获取该目录大小
     *
     * @param file 目录
     * @return 文件大小
     */
    fun getFolderSize(file: File): Long {
        var size = 0L
        file.listFiles()?.forEach {
            size += if (it.isDirectory) {
                getFolderSize(it)
            } else {
                it.length()
            }
        }
        return size
    }

    /**
     * 获取格式化后的文件大小
     *
     * @param size 文件byte
     * @return MB或者GB
     */
    fun getFormatSize(size: Long): String {
        val megabyte = size.toDouble() / SIZE_UNIT / SIZE_UNIT
        val gigabyte = megabyte / SIZE_UNIT

        if (gigabyte < 1) {
            return BigDecimal(megabyte).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + SUFFIX_MB
        }
        return BigDecimal(gigabyte).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + SUFFIX_GB
    }

    fun unZip(path: String, zipName: String, targetPath: String? = path): Boolean {
        val `is`: InputStream
        var zis: ZipInputStream? = null
        try {
            var filename: String
            `is` = FileInputStream(path + zipName)
            zis = ZipInputStream(BufferedInputStream(`is`))
            var ze: ZipEntry?
            val buffer = ByteArray(1024)
            var count: Int

            ze = zis.nextEntry
            while (ze != null) {
                filename = ze.name

                val fmd = File(targetPath + filename)
                if (ze.isDirectory) {
                    fmd.mkdirs()
                    ze = zis.nextEntry
                    continue
                }

                if (!fmd.parentFile.exists()) {
                    fmd.parentFile.mkdirs()
                }

                val fOut = FileOutputStream(targetPath + filename)
                try {
                    count = zis.read(buffer)
                    while (count != -1) {
                        fOut.write(buffer, 0, count)
                        count = zis.read(buffer)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    return false
                } finally {
                    fOut.close()
                    zis.closeEntry()
                }

                ze = zis.nextEntry
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            try {
                zis?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        return true
    }

}
