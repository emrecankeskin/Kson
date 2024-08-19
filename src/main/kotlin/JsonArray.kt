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

        while(true){

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
                    value = parseBoolean(c)
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

        while(true){
            val c: Char = str[ptr]
            //if end of value
            if(c == '\"'){

                return JsonPrimitive(str.substring(start,ptr++))
            }
            if(c == '\\'){
                ptr++
            }
            ptr++
        }


    }




    /**
     * Returns Double or Long
     *
     * */
    //TODO find better ways to parse number
    private fun parseNumber(): JsonElement{
        val start = ptr
        var dot = false

        while(true){
            val c: Char = str[ptr]
            when{
                c.isDigit() || c == '-' -> {
                    ptr++
                }
                c == '.' || c == 'e' || c == 'E' -> {
                    dot = true
                    ptr++
                }
                //TODO bottleneck
                else -> {
                    return if(dot){
                        //JsonPrimitive(str.substring(start,ptr).toDouble())
                        JsonPrimitive(str.substring(start,ptr).toDouble())
                    }else{
                        JsonPrimitive(str.substring(start,ptr).toLong())
                    }
                }
            }
        }

    }

    private fun parseBoolean(firstChar: Char): JsonPrimitive{

        while(true){
            val c: Char = str[ptr]
            if(c == ',' || c == '}'){
                //bottleneck
                when(firstChar){
                    't' -> return JsonPrimitive(true)
                    'f' -> return JsonPrimitive(false)
                    'n' -> return JsonPrimitive(null)
                }

            }
            ptr++

        }

    }


    fun get(index: Int): JsonElement? {
        return if (index < list.size) list[index] else null
    }


    inline fun <reified T> getAs(index: Int): T? {
        return get(index) as? T
    }
}