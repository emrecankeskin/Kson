import java.util.*

//https://www.microfocus.com/documentation/silk-performer/195/en/silkperformer-195-webhelp-en/GUID-0847DE13-2A2F-44F2-A6E7-214CD703BF84.html

class JsonArray(
    private val str: String,
    internal val start: Int = 0
): JsonElement(){
    
    val list = LinkedList<JsonElement>()
    internal var ptr = 0

    init {
        this.ptr = start
        this.parse()
    }

    private fun parse(){
        ptr++
        var value: JsonElement

        while(ptr < str.length){

            val c = str[ptr]
            when{
                 c.isDigit() || c == '-' ->{

                     value = parseNumber()
                     list.add(value)
                }
                c == '"' ->{
                    value = parseString()
                    list.add(value)
                }
                c == 't' || c ==  'f' || c == 'n' -> {
                    value = parseBoolean()
                    list.add(value)
                }
                c == '{' -> {

                    value = JsonObject(str, ptr)
                    ptr = value.ptr + 1
                    list.add(value)

                }

                c == '[' -> {
                    value = JsonArray(str, ptr)
                    ptr = value.ptr + 1
                    list.add(value)

                }
                c == ',' || c == ' ' || c =='\n' || c == '\t' || c== '}' ->{
                    ptr++
                    continue
                }

                c == ']' ->{
                    break
                }
            }
        }
    }



    private fun parseString(): JsonElement{
        // skip starting of string
        val start = ++ptr

        while(ptr < str.length){
            val c: Char = str[ptr]
            //if end of value
            if(c == '\"'){
                ptr++
                return JsonPrimitive(str.substring(start,ptr-1))
            }

            if(c == '\\'){
                ptr++
            }
            ptr++
        }

        return JsonPrimitive("[ERROR]")

    }


    /**
     * Returns Double or Long
     *
     * */
    private fun parseNumber(): JsonElement{
        val start = ptr
        var dot = false

        while(ptr<str.length){
            val c: Char = str[ptr]
            when{
                c.isDigit() || c == '-' -> {
                    ptr++
                }
                c == '.' || c == 'e' || c == 'E' -> {
                    dot = true
                    ptr++
                }
                else -> {
                    return if(dot){
                        JsonPrimitive(str.substring(start,ptr).toDouble())
                    }else{
                        JsonPrimitive(str.substring(start,ptr).toLong())
                    }
                }
            }
        }

        return JsonPrimitive(-1)
    }



    private fun parseBoolean(): JsonElement{
        val start = ptr

        while(ptr<str.length){
            val c: Char = str[ptr]
            if(c == ',' || c == '}'){
                return JsonPrimitive(str.substring(start,ptr).toBoolean())
            }
            ptr++

        }

        return JsonPrimitive(false)
    }



    fun get(index: Int): JsonElement? {
        return if (index < list.size) list[index] else null
    }


    inline fun <reified T> getAs(index: Int): T? {
        return get(index) as? T
    }
}