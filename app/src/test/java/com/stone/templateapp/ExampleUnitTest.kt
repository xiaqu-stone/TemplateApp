package com.stone.templateapp

import com.stone.templateapp.util.CompressUtils
import com.stone.templateapp.util.EDcryptUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun testMD5() {
        //959bf3c41b5b5aed7fb76eaeac7d8a3b
        assertEquals("959bf3c41b5b5aed7fb76eaeac7d8a3b", EDcryptUtils.toMD5("日了狗"))
    }


    @Test
    fun testFileMD5() {
//        CRC-32	71430673
//        MD5 Hash	49d5c8a4bd81219a3554ca1db9013cf8
//        SHA1 Hash	d899d9343d870f454317842f886aade1cb9d4238
//        SHA256 Hash	3541aa23675312943d270991792446b3a58b2c95d30002706e5e8d9107429347
        assertEquals("49d5c8a4bd81219a3554ca1db9013cf8", EDcryptUtils.toMD5(File("/Users/mac/Downloads/bundlejs.zip")))
    }

    @Test
    fun testUnZip() {
//        CompressUtils.unzip("/Users/mac/Downloads/20180710143118465130018.zip","/Users/mac/Downloads/ziptest")
        CompressUtils.unzip("/Users/mac/Downloads/my.zip","/Users/mac/Downloads/ziptestmy")
//        CompressUtils.unzip("/Users/mac/Downloads/zip/归档.zip", "/Users/mac/Downloads/zip")
//        20180710143118465130018.zip
    }

    @Test
    fun testZip() {
//        CompressUtils.zip("/Users/mac/Downloads/zip/")
    }
}
