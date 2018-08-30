package com.stone.templateapp

import org.junit.Test

/**
 * Created By: sqq
 * Created Time: 18/7/6 下午3:03.
 */
class KotlinTest {
    @Test
    fun testCollection() {
        "ddfdfff".trimEnd()
    }
}

val String.lastChar: Char
    get() = get(length - 1)


var StringBuilder.lastChar: Char
    get() = get(length - 1)
    set(value) {
        this.setCharAt(length - 1, value)
    }