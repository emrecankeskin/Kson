import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.io.InputStream

class JsonArrayTest {

    private val filePath = "../Kson/src/main/test/patates"
    private val ins: InputStream = File(filePath).inputStream()
    private val str = ins.bufferedReader().use { it.readText() }
    private val parser = JsonArray(str)

    @Test
    fun getValues() {
        val arr = listOf(10, 20, "true", 40, "Nested Array")
        val obj = parser.list[0] as JsonObject
        val jArr = (parser.list[1] as JsonArray).list
        assertEquals(obj.values["Name"],"Nested Object" )

        for(i in arr.indices){
            assertEquals(arr[i],jArr[i])
        }
    }
}