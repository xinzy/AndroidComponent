package com.xinzy.component

import org.junit.Test

import org.junit.Assert.*
import java.security.MessageDigest

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, (2 + 2).toLong())
    }

    @Test
    fun testNull() {
        var index: Int? = 0
        val obj: Any? = null
        index = obj as? Int
        println(index)
    }

    @Test
    fun testHash() {
        val hash = MessageDigest
                .getInstance("MD5")
                .digest("s878926199a".toByteArray())
                .map { String.format("%02X", it) }
                .joinToString(separator = "")

        println(hash)
    }
}