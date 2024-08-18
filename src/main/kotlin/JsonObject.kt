import JsonParser.Companion.wsLookUp

/**
 * There is no validation for anything, it assumes that coming json object is valid
 *
 *
 * */

class JsonObject(
    private val str: String,
    internal val start: Int = 0
) : JsonElement() {

    val values = LinkedHashMap<String,JsonElement>()
    internal var ptr: Int

    //private val wsLookUp = BooleanArray(256){false}
    

    init {


        this.ptr = start
        this.parse()
    }

    //Used enums with state-machine like solution, but it was 10 ms slower with 608k char file
    private fun parse(){

        // 1 0 -> 0 0 -> 1 1
        var keyParse = true
        var value: JsonElement
        var key  = ""

        while(str[ptr] == '{'){
            ptr++
        }
        //ptr < str.length
        while(ptr < str.length){

            val c = str[ptr]
            when{
                c.isDigit() || c == '-' ->{
                    keyParse = true
                    value = parseNumber()
                    values[key] = value
                }
//                digitLookUp[c.code] || digitLookUp[c.code] ->{
//                    keyParse = true
//                    value = parseNumber()
//                    values[key] = value
//                }
                c == '"' ->{
                    if(keyParse){
                        keyParse = false
                        key = parseKey()
                    }else{
                        keyParse = true
                        value = parseString()
                        values[key] = value
                    }

                }
                c == 't' ||  c == 'f' || c == 'n' -> {
                    keyParse = true
                    value = parseBoolean()
                    values[key] = value
                }
                c == '{' -> {

                    keyParse = true
                    value = JsonObject(str, ptr)
                    ptr = value.ptr + 1
                    values[key] = value
                }

                c == '[' -> {
                    keyParse = true
                    value = JsonArray(str, ptr)
                    ptr = value.ptr + 1
                    values[key] = value
                }
//                c == ','|| c == ' ' || c== ':' || c == '\n' || c =='\t' ->{
//                    ptr++
//                    continue
//                }
                wsLookUp[c.code] ->{
                    ptr++
                    continue
                }

                c == '}' ->{
                    break
                }
            }

        }

    }


    //TODO check if this is better than returning String?
    //TODO can i build this with stringbuilder and pass escape chars
    private fun parseString(): JsonPrimitive{
        // skip starting of string
        val start = ++ptr

        while(ptr < str.length){
            val c: Char = str[ptr]
            //if end of value
            if(c == '\"'){
                ptr++
                return JsonPrimitive(str.substring(start,ptr-1))
            }
            //&& ptr+1 < str.length
            if(c == '\\'){
                ptr++
            }
            ptr++
        }

        return JsonPrimitive("")

    }

    //TODO instead of returning string return the index of key
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
            //&& ptr+1 < str.length
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
    private fun parseNumber(): JsonPrimitive{
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


    private fun parseBoolean(): JsonPrimitive{
        val start = ptr
        while(true){
            val c: Char = str[ptr]
            if(c == ',' || c == '}'){
                return JsonPrimitive(str.substring(start,ptr).toBoolean())
            }
            ptr++

        }

    }


    fun getSize(): Int = values.size

    /**
     * Returns null Any? if key is not present
     *
     * */
    fun get(key: String): JsonElement?  = values[key]


    inline fun <reified T> getAs(key: String): T? = get(key) as? T

}