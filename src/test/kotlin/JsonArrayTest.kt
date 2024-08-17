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
        val arr = listOf(10L, 20L, true, 40L, "Nested Array")
        val obj = parser.values["obj"] as JsonObject
        val jArr = (parser.values["arr"] as JsonArray).list
        assertEquals(obj.values["Name"],"Nested Object" )

        for(i in arr.indices){
            assertEquals(arr[i],jArr[i])
        }
    }
}