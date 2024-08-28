import JsonParser.Companion.wsLookUp

/**
 * There is no validation for anything, it assumes that coming [JsonObject] is valid
 * @param str source string of json
 * @param start start index of json elements
 *
 * @return JsonElement
 * */

class JsonObject(
    private val str: String,
    private val start: Int = 0
) : JsonElement(),Iterable<Map.Entry<String,JsonElement>> {

    private val values = LinkedHashMap<String,JsonElement>()

    @PublishedApi internal var ptr: Int


    init {

        this.ptr = start
        this.parse()
    }

    /**
     * If given json is not valid it will stick in infinite loop
     * */
    private fun parse(){

        var keyParse = true
        var value: JsonElement
        var key  = ""

        while(str[ptr] == '{'){
            ptr++
        }


        while(true){

            val c = str[ptr]

            when{
                c.isDigit() || c == '-'->{
                    keyParse = true
                    value = parseNumber()
                    values[key] = value
                }
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
                    value = parseBoolean(c)
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

                //c == ','|| c == ' ' || c== ':' || c == '\n' || c =='\t'
                wsLookUp[c.code] ->{
                    ptr++
                }

                c == '}' ->{
                    break
                }
            }

        }

    }



    /**
     * Starts from ptr+1 value and scans for '"'  char
     * */
    private fun parseString(): JsonPrimitive{
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


    private fun parseKey(): String{
        // skip starting of string

        val start = ++ptr

        while(true){
            val c = str[ptr]
            //if end of value
            if(c == '\"'){
                ptr++
                return str.substring(start,ptr-1) //bottleneck
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


    /**
     * Moving cursor to end of value , object or array returns according to first chzr
     *
     * @param firstChar t f n case-insensitive
     * */
    private fun parseBoolean(firstChar: Char): JsonPrimitive{

        while(true){
            val c: Char = str[ptr]
            if(c == ',' || c == '}' || c == ']'){
                when(firstChar.lowercaseChar()){
                    't' -> return JsonPrimitive(true)
                    'f' -> return JsonPrimitive(false)
                    'n' -> return JsonPrimitive(null)
                }

            }
            ptr++

        }

    }


    //TEST





    fun getSize(): Int = values.size

    /**
     * Returns null JsonElement? if key is not present
     *
     * */
    operator fun get(s: String):  JsonElement? {
        return values[s]
    }


    inline fun <reified T> getAs(key: String): T? = get(key) as? T

    override fun iterator(): Iterator<Map.Entry<String, JsonElement>> {
        return values.entries.iterator()
    }


}