package com.tb.tblibrarydemo

import com.tb.library.tbExtend.tb2Json
import com.tb.library.tbExtend.tb2Object
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        println("person------->${Person().tb2Json()}")
        val str = "{\"name\":\"null\",\"age\":null,\"mList\":null}"
        println("person------->${str.tb2Object<Person>().tb2Json()}")

    }
}


data class Person(
    var name: String? = null,
    var age: Int? = null,
    var isBoy: Boolean? = null,
    var size: Float? = null,
    var color: Long? = null,
    var mList: List<String>? = arrayListOf("asda")
)
