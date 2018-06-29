package com.stone.templateapp.util

import com.stone.log.Logs
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Created By: sqq
 * Created Time: 18/5/22 下午2:36.
 */
object CompressUtils {

    /**
     * zip压缩文件夹及文件
     * @param zipFile 指定压缩后存放的目录及其文件名
     * @param srcFile 被压缩的源文件，可变参数，可指定多个
     */
    fun zip(zipFile: File, vararg srcFile: File) {
        val out = ZipOutputStream(zipFile.outputStream())
        zip(out, "", srcFile, true)
        out.close()
    }

    /**
     * zip压缩单个文件或者单个文件夹
     * 压缩后的文件以源文件的 name + .zip
     */
    fun zip(srcFile: String) {
        zip(File(srcFile))
    }

    /**
     * zip压缩单个文件或者单个文件夹
     * 压缩后的文件以源文件的 name + .zip
     */
    fun zip(srcFile: File) {
        val zipPath: String = if (srcFile.name.contains(".")) {
            srcFile.name.substring(0, srcFile.name.lastIndexOf("."))
        } else {
            srcFile.name
        }
        zip(File(srcFile.parentFile, "$zipPath.zip"), srcFile)
    }

    private fun zip(out: ZipOutputStream, path: String, srcFile: Array<out File>, isOriginSrc: Boolean) {
        var parent = ""
        if (path.isNotEmpty() && !path.replace("\\*", "/").endsWith("/")) {
            parent = path.plus("/")
        }
//        println("parent = $parent")
//        println("path = $path")

        val buffer = ByteArray(2 * 1024 * 1024)
        srcFile.iterator().forEach {
            println("srcFile: ${it.name}")
            if (it.isDirectory) {//是目录 遍历目录
                var entryName = parent + it.name
                if (!entryName.endsWith("/")) {
                    entryName += "/"
                }
                if (!isOriginSrc) {
                    out.putNextEntry(ZipEntry(entryName))
                }
                zip(out, parent + it.name, it.listFiles(), false)
            } else {//是文件 直接压缩
                out.putNextEntry(ZipEntry(parent + it.name))
                it.inputStream().use {
                    var len = it.read(buffer)
                    while (len > 0) {
                        out.write(buffer, 0, len)
                        len = it.read(buffer)
                    }
                    out.closeEntry()
                }
            }
        }
    }

    fun unzip(zipFile: String, destFile: String) {
        unzip(File(zipFile), File(destFile))
    }

    /**
     * 解压文件至当前目录
     */
    fun unzip(zipFile: File) {
        unzip(zipFile, zipFile.parentFile)
    }

    /**
     * 解压文件至当前目录
     */
    fun unzip(zipFile: String) {
        unzip(File(zipFile), File(zipFile).parentFile)
    }

    /**
     * unzip解压到指定目录
     * @param zipFile 带解压zip文件
     * @param destFile 指定目录
     */
    fun unzip(zipFile: File, destFile: File) {
        if (!checkZipFile(zipFile)) {
            Logs.e("zip 文件无效，请确保被解压的文件为zip文件")
            return
        }

        checkDestFile(destFile)

//        println("======开始解压========")

        val zip = ZipFile(zipFile)

//        println("zip.name = ${zip.name}")//zip文件的全路径

        //根据压缩文件名，获取解压文件的父目录名
//        val name = ZipFile(zipFile).name.substring(zip.name.lastIndexOf('/') + 1, zip.name.lastIndexOf('.'))
//        var destFile = File(destFile, name)
//        println("name = $name")
        zip.entries().iterator().forEach {
            println("zip entry = ${it.name}")
            val inputStream = zip.getInputStream(it)
            val outFile = File(destFile, it.name)
//            println(outFile.path)
            if (it.isDirectory) {
                checkDestFile(outFile)
            } else {//只有不是目录，才进行下面的解压流程
                outFile.outputStream().buffered(10 * 1024 * 1024)
                        .use {
                            val buffer = ByteArray(2 * 1024 * 1024)
                            var len = inputStream.read(buffer)
                            while (len > 0) {
                                it.write(buffer, 0, len)
                                len = inputStream.read(buffer)
                            }
                            it.flush()
                            inputStream.close()
                        }
            }
        }

//        println("======结束解压========")

    }

    private fun checkZipFile(file: File): Boolean {
        return file.exists() && file.isFile && file.name.endsWith(".zip", true)
    }

    private fun checkDestFile(file: File) {
        if (!file.exists()) file.mkdirs()
        if (!file.isDirectory) {
            file.delete()
            file.mkdirs()
        }
    }


}