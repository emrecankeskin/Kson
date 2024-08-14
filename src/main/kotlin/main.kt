
import java.io.File
import java.io.InputStream


fun main(){


    val filePath = "/home/emrecan/IdeaProjects/Kson/src/main/domates"
    val ins: InputStream = File(filePath).inputStream()
    val str = ins.bufferedReader().use { it.readText() }
    val parser = JsonObject(str)

    for((k,v) in parser.values){
        println("$k $v")
    }


}

