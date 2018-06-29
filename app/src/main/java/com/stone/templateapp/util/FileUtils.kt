package com.stone.templateapp.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.*

object FileUtils {

    private const val APP_DIRECTORY = "RONGYILAI"

    /**
     * Returns whether the SD card is available.
     */
    val isSdCardAvailable: Boolean
        get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()


    fun getDefaultDownFile(context: Context, fileName: String): File {
        //获取私有外部存储，可以避免关于的权限的适配问题
        var destFileDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (destFileDir == null) {//获取不到外部存储目录,使用内部存储
            destFileDir = context.filesDir
        }
        return File(destFileDir, fileName)
    }

    /**
     * Ensures the given directory exists by creating it and its parents if necessary.
     *
     * @return whether the directory exists (either already existed or was successfully created)
     */
    private fun ensureDirectoryExists(dir: File): Boolean {
        if (dir.exists() && dir.isDirectory) {
            return true
        }
        return dir.mkdirs()
    }

    private fun ensureDirectoryExists(dir: String?): Boolean {
        return ensureDirectoryExists(File(dir))
    }

    /**
     * Builds a path inside the My Tracks directory in the SD card.
     *
     * @param components the path components inside the mytracks directory
     * @return the full path to the destination
     */
    fun buildExternalDirectoryPath(vararg components: String): String? {
        val dirNameBuilder = StringBuilder()
        dirNameBuilder.append(Environment.getExternalStorageDirectory())
        dirNameBuilder.append(File.separatorChar)
        dirNameBuilder.append(APP_DIRECTORY)
        for (component in components) {
            dirNameBuilder.append(File.separatorChar)
            dirNameBuilder.append(component)
        }
        var path: String? = dirNameBuilder.toString()
        var exist = ensureDirectoryExists(path)
        if (!exist) {
            val path2 = builderMntDirectoryPath(*components)
            exist = ensureDirectoryExists(path2)
            if (exist) {
                path = path2
            } else {
                path = null
//                MyToast.getInstance().show("操作失败，应用无法在SD卡创建文件。")
            }
        }
        return path
    }

    /**
     * 为了解决一些手机Environment.getExternalStorageDirectory()获取的路径没有权限问题
     *
     * @param components
     * @return
     */
    private fun builderMntDirectoryPath(vararg components: String): String {
        val dirNameBuilder = StringBuilder()
        dirNameBuilder.append(File.separatorChar)
        dirNameBuilder.append("mnt")
        dirNameBuilder.append(File.separatorChar)
        dirNameBuilder.append("sdcard")
        dirNameBuilder.append(File.separatorChar)
        dirNameBuilder.append(APP_DIRECTORY)
        for (component in components) {
            dirNameBuilder.append(File.separatorChar)
            dirNameBuilder.append(component)
        }
        return dirNameBuilder.toString()
    }


    fun saveFile(source: File, destination: File): String {
        val fis: FileInputStream?
        val fos: FileOutputStream?
        val savePath = destination.absolutePath
        try {
            fis = FileInputStream(source)
            fos = FileOutputStream(destination)
            val buffer = ByteArray(1024)
            var read: Int

            fis.use {
                while (run { read = fis.read(buffer);read != -1 }) {
                    fos.use { fos.write(buffer) }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

//        fis.use {
//            while (let { read = fis.read(buffer);read != -1 }) {
//                fos.use { fos.write(buffer) }
//            }
//        }
        return savePath
    }

    fun saveFile(source: Bitmap, destination: File): String {
        val fos: FileOutputStream = FileOutputStream(destination)
        var savePath = destination.absolutePath

        try {
            source.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            savePath = ""
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        return savePath
    }

    /**
     * 通过缓冲流 写入大文件
     */
    fun saveBigFile(input: InputStream, destination: File) {
        checkFile(destination)
//        FileOutputStream(destination).use {
//            val buffer = ByteArray(1024 * 1024 * 2)
//            var len = input.read(buffer)
//            while (len != -1) {
//                it.write(buffer, 0, len)
//                len = input.read(buffer)
//            }
//            it.flush()
//            input.close()
//        }
        destination.outputStream().buffered(DEFAULT_BUFFER_SIZE)
                .use {
                    val bufferedInputStream = input.buffered(2 * 1024 * 1024)
                    var len = bufferedInputStream.read()
                    while (len != -1) {
                        it.write(len)
                        len = input.read()
                    }
                    it.flush()
                    input.close()
                }

    }


    private const val DEFAULT_BUFFER_SIZE: Int = 10 * 1024 * 1024
    /**
     * 快速保存小文件。注意文件会被全部读到内存中，所以仅仅适用小文件;
     * 10M
     */
    fun saveSmallFile(input: InputStream, destination: File) {
        checkFile(destination)
        destination.outputStream().buffered(DEFAULT_BUFFER_SIZE)
                .use {
                    it.write(input.readBytes(DEFAULT_BUFFER_SIZE))
                    it.flush()
                    input.close()
                }
    }

    /**
     * 确保文件路径有效，删除已存在的文件
     */
    fun checkFile(file: File) {
        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        if (file.exists()) file.delete()
    }

}
