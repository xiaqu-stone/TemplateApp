package com.stone.templateapp

import org.junit.Assert.assertEquals
import org.junit.Test
import com.stone.templateapp.util.AppUtil

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun testMD5() {
        //959bf3c41b5b5aed7fb76eaeac7d8a3b
        assertEquals("959bf3c41b5b5aed7fb76eaeac7d8a3b", AppUtil.toMD5("日了狗"))
    }
}
