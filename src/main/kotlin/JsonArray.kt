import java.util.*

//https://www.microfocus.com/documentation/silk-performer/195/en/silkperformer-195-webhelp-en/GUID-0847DE13-2A2F-44F2-A6E7-214CD703BF84.html



class JsonArray1(
    private val str: String,
    private val start: Int = 0
): JsonElement(),Iterable<JsonElement>{


    private val list = LinkedList<JsonElement>()
    @PublishedApi internal var ptr = 0

    init {
        this.ptr = start
        this.parse()
    }

    companion object{

        val wsLookUp = BooleanArray(256).apply {

            /**
             * white space chars
             * */
            this[9] = true
            this[10] = true
            this[32] = true
            this[44] = true
            this[58] = true
        }

        const val OPEN_CURLY = '{'
        const val CLOSE_CURLY = '}'

        const val OPEN_BRACKET = '['
        const val CLOSE_BRACKET = ']'

        const val CAPITAL_E = 'E'
        const val SMALL_E = 'e'

        const val SMALL_T = 't'
        const val SMALL_F = 'f'
        const val SMALL_N = 'n'

        const val DOT = '.'
        const val COMMA = ','

        const val QUOT = '"'
        const val DASH = '-'

    }


    private fun parse(){
        ptr++
        var value: JsonElement

        while(true){

            val c = str[ptr]
            when{

                c.isDigit() || c == DASH ->{
                     value = parseNumber()
                     list.add(value)
                }
                c == QUOT ->{
                    value = parseString()
                    list.add(value)
                }
                c == SMALL_T || c ==  SMALL_F || c == SMALL_N  -> {
                    value = parseBoolean(c)
                    list.add(value)
                }
                c == OPEN_CURLY -> {
                    value = JsonObject(str, ptr)
                    ptr = value.ptr + 1
                    list.add(value)
                }

                c == OPEN_BRACKET -> {
                    value = JsonArray(str, ptr)
                    ptr = value.ptr + 1
                    list.add(value)

                }
                //c == ',' || c == ' ' || c =='\n' || c == '\t' || c== '}'
                c == ',' || c == ' ' || c =='\n' || c == '\t' || c== '}' ->{
                    ptr++
                    continue
                }

                c == CLOSE_BRACKET ->{
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
                c.isDigit() || c == DASH -> {
                    ptr++
                }
                c == DOT || c == SMALL_E || c == CAPITAL_E -> {
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

    private fun parseBoolean(firstChar: Char): JsonPrimitive{

        while(true){
            val c: Char = str[ptr]
            if(c == COMMA || c == CLOSE_CURLY || c == CLOSE_BRACKET){
                when(firstChar.lowercaseChar()){
                    SMALL_T -> return JsonPrimitive(true)
                    SMALL_F -> return JsonPrimitive(false)
                    SMALL_N -> return JsonPrimitive(null)
                }

            }
            ptr++

        }

    }


    /**
     * @param index
     * Returns the JsonElement at index
     * If index exceeds number of elements it returns null
     * */
    operator fun get(index: Int): JsonElement? {
        return if (index < list.size) list[index] else null
    }

    fun size() = list.size

    /**
     * @param index
     * Returns the T at index
     * If index exceeds number of elements it returns null
     * */
    inline fun <reified T> getAs(index: Int): T? {
        return get(index) as? T
    }

    override fun iterator(): Iterator<JsonElement> {
        return list.iterator()
    }
}