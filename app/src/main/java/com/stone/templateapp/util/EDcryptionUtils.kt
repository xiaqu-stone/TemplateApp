package com.stone.templateapp.util

import java.io.File
import java.security.DigestInputStream
import java.security.MessageDigest

/**
 * Created By: sqq
 * Created Time: 18/5/22 上午11:31.
 */
object EDcryptUtils {

    /**
     * MD5加密字符串
     */
    fun toMD5(string: String): String {
        return toHex(MessageDigest.getInstance("MD5").digest(
                string.toByteArray(charset("UTF-8"))))
    }

    /**
     * 获取文件的MD5值
     *
     * Note: 此方法仅可在小文件时，推荐使用；由于需要会把文件的全部内部读到内存中，转换为字节数组，所以大文件会占用很大的内存；
     *          不考虑内存占用的情况，基于内部限制，文件不可超过2G
     */
    fun toMD5(file: File): String {
        if (!file.exists() || !file.isFile) return ""
        return toHex(MessageDigest.getInstance("MD5").digest(file.readBytes()))
    }

    /**
     * 获取大文件的MD5值
     */
    fun toMD5BigFile(file: File): String {
        if (!file.exists() || !file.isFile) return ""
        val digestInputStream = DigestInputStream(file.inputStream(), MessageDigest.getInstance("MD5"))
        val buffer = ByteArray(2 * 1024 * 1024)
        while (digestInputStream.read(buffer) > 0);
        return toHex(digestInputStream.messageDigest.digest())
    }

    /**
     * 将字节数组转换为十六进制字符串
     */
    fun toHex(byteArray: ByteArray): String {
        val hex = StringBuilder(byteArray.size * 2)
        for (b in byteArray) {
//            println("origin byte is $b, 位运算后 ${b.toInt() and 0xFF}, hex运算之后 ${Integer.toHexString(b.toInt() and 0xFF)}")
            if ((b.toInt() and 0xFF) < 0x10) hex.append("0")
            hex.append(Integer.toHexString(b.toInt() and 0xFF))
        }
        return hex.toString()
    }

}