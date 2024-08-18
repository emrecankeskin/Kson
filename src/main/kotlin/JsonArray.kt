//https://www.microfocus.com/documentation/silk-performer/195/en/silkperformer-195-webhelp-en/GUID-0847DE13-2A2F-44F2-A6E7-214CD703BF84.html

class JsonArray(
    private val str: String,
    internal val start: Int = 0
): JsonElement(){
    
    val list = ArrayList<JsonElement>()
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
            when(c){
                 '0','1','2','3','4','5','6','7','8','9','-' ->{

                     value = parseNumber()
                     list.add(value)
                }
                '"' ->{
                    value = parseString()
                    list.add(value)
                }
                't' , 'f' ,'n' -> {
                    value = parseBoolean()
                    list.add(value)
                }
                '{' -> {

                    value = JsonObject(str, ptr)
                    ptr = value.ptr + 1
                    list.add(value)

                }

                '[' -> {
                    value = JsonArray(str, ptr)
                    ptr = value.ptr + 1
                    list.add(value)

                }
                ',',' ','\n','\t','}' ->{
                    ptr++
                    continue
                }
                ']' ->{
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
                ptr++
                return JsonPrimitive(str.substring(start,ptr-1))
            }


            if(c == '\\'){
                ptr++
            }
            ptr++
        }

    }

    private fun parseKey(): String{
        // skip starting of string
        val start = ++ptr

        while(true){
            val c: Char = str[ptr]
            //if end of value
            if(c == '\"'){
                ptr++
                return str.substring(start,ptr-1)
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
                else -> {
                    return if(dot){
                        JsonPrimitive(str.substring(start,ptr).toDouble())
                    }else{
                        JsonPrimitive(str.substring(start,ptr).toLong())
                    }
                }
            }
        }

    }
    private fun parseBoolean(): JsonElement{
        val start = ptr

        while(true){
            val c: Char = str[ptr]
            if(c == ',' || c == '}'){
                return JsonPrimitive(str.substring(start,ptr).toBoolean())
            }
            ptr++

        }

    }



    fun get(index: Int): Any? {
        return if (index in list.indices) list[index] else null
    }

    inline fun <reified T> getAs(index: Int): T? {
        return get(index) as? T
    }
}