# Small JSON scraping library written in Kotlin.

**Warning! This library will fail if you try to parse invalid JSON strings.**


**Example**
```Kotlin

import java.io.File
import java.io.InputStream

fun main(){
    val filePath = "../Kson/src/main/test/twitter00.json"
    val ins: InputStream = File(filePath).inputStream()
    val str = ins.bufferedReader().use { it.readText() }

    val json = JsonParser.parseFromString(str)
    println(json.asJsonArray().size())

}

```
