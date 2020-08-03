package com.fewwind.word

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import java.io.*

/**
 * Created by huangjun on 2017/3/7.
 */
object FileUtils {
    private const val TAG = "FileUtils"
    fun getFile(path: String): File? {
        try {
            val file = File(path)
            val dir = file.parentFile
            if (dir == null) {
                Log.e(
                    TAG,
                    "file's parent dir is null, path=" + file.canonicalPath
                )
                return null
            }
            if (!dir.exists()) {
                if (dir.parentFile.exists()) {
                    dir.mkdir() // dir父目录存在用mkDir
                } else {
                    dir.mkdirs() // dir父目录不存在用mkDirs
                }
            }
            if (!file.exists() && !file.createNewFile()) {
                Log.e(
                    TAG,
                    "can not create dest file, path=$path"
                )
                return null
            }
            return file
        } catch (e: Throwable) {
            Log.e(
                TAG,
                "create dest file error, path=$path",
                e
            )
        }
        return null
    }

    fun appendFile(message: String?, path: String?): Boolean {
        if (TextUtils.isEmpty(message)) {
            return false
        }
        if (TextUtils.isEmpty(path)) {
            return false
        }
        var written = false
        try {
            val fw = BufferedWriter(FileWriter(path, true))
            fw.write(message)
            fw.flush()
            fw.close()
            written = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return written
    }

    fun wirteFile(message: MutableList<WordInfo>, path: String?): Boolean {
        if (message.isEmpty()) {
            return false
        }
        if (TextUtils.isEmpty(path)) {
            return false
        }
        var written = false
        try {
            val fw = BufferedWriter(OutputStreamWriter(FileOutputStream(File(path)), "GBK"))
            message.forEach {
                fw.write("${it.word}   ${it.count}")
                fw.newLine()
//                fw.write("\r\n")
            }

            fw.flush()
            fw.close()
            written = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return written
    }


    fun appendFile(message: ByteArray?, path: String?): Boolean {
        if (message == null || message.isEmpty()) {
            return false
        }
        if (TextUtils.isEmpty(path)) {
            return false
        }
        var written = false
        try {
            val fw = FileOutputStream(path, true)
            fw.write(message)
            fw.close()
            written = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return written
    }

    @Synchronized
    fun shrink(logPath: String, maxLength: Int, baseLength: Int) {
        val file = File(logPath)
        if (file.length() < maxLength) {
            return
        } else if (file.length() > Int.MAX_VALUE) {
            file.delete()
            return
        }
        val out = File(logPath + "_tmp")
        var fis: FileInputStream? = null
        var fos: FileOutputStream? = null
        try {
            fis = FileInputStream(file)
            fos = FileOutputStream(out)
            val input = fis.channel
            input.position(file.length() - baseLength)
            val output = fos.channel
            output.transferFrom(fis.channel, 0, baseLength.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close(fis)
            close(fos)
        }
        if (out.exists()) {
            if (file.delete()) {
                out.renameTo(file)
            }
        }
    }

    fun close(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getFilePath(dirPath: String, fileName: String): String {
        val dir = File(dirPath)
        if (!dir.exists()) {
            if (dir.parentFile.exists()) {
                dir.mkdir() // dir父目录存在用mkDir
            } else {
                dir.mkdirs() // dir父目录不存在用mkDirs
            }
        }
        return dirPath + File.separator + fileName
    }

    // 是否包含扩展名
    fun hasExtension(filename: String): Boolean {
        val dot = filename.lastIndexOf('.')
        return dot > -1 && dot < filename.length - 1
    }

    // 获取文件扩展名
    fun getExtensionName(filename: String?): String {
        if (filename != null && filename.isNotEmpty()) {
            val dot = filename.lastIndexOf('.')
            if (dot > -1 && dot < filename.length - 1) {
                return filename.substring(dot + 1)
            }
        }
        return ""
    }

    // 获取文件名
    fun getFileNameFromPath(filepath: String?): String? {
        if (filepath != null && filepath.isNotEmpty()) {
            val sep = filepath.lastIndexOf('/')
            if (sep > -1 && sep < filepath.length - 1) {
                return filepath.substring(sep + 1)
            }
        }
        return filepath
    }

    // 获取不带扩展名的文件名
    fun getFileNameNoEx(filename: String?): String? {
        if (filename != null && filename.isNotEmpty()) {
            val dot = filename.lastIndexOf('.')
            if (dot > -1 && dot < filename.length) {
                return filename.substring(0, dot)
            }
        }
        return filename
    }

    fun getExternalPackageDirectory(context: Context): String {
        val externalPath =
            Environment.getExternalStorageDirectory().path
        return externalPath + File.separator + context.packageName
    }
}