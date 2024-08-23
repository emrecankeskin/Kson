import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.io.InputStream

class JsonArrayTest {

    private val filePath = "../Kson/src/main/test/patates"
    private val ins: InputStream = File(filePath).inputStream()
    private val str = ins.bufferedReader().use { it.readText() }
    private val parser = JsonObject(str)

    @Test
    fun getValues() {
        val arr = listOf(
            JsonPrimitive(10L),
            JsonPrimitive(20L),
            JsonPrimitive(true),
            JsonPrimitive(40L),
            JsonPrimitive("Nested Array"))
        val obj = parser.getAs<JsonObject>("obj")
        val jArr = parser.getAs<JsonArray>("arr")
        assertEquals(obj?.getAs<JsonPrimitive>("Name").toString(),"Nested Object" )

        for(i in arr.indices){
            assertEquals(arr[i].value, jArr?.getAs<JsonPrimitive>(i)?.value)
        }
    }
}