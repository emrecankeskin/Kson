import JsonCharTable.Companion.CAPITAL_E
import JsonCharTable.Companion.CLOSE_BRACKET
import JsonCharTable.Companion.CLOSE_CURLY
import JsonCharTable.Companion.COMMA
import JsonCharTable.Companion.DASH
import JsonCharTable.Companion.DOT
import JsonCharTable.Companion.OPEN_BRACKET
import JsonCharTable.Companion.OPEN_CURLY
import JsonCharTable.Companion.QUOT
import JsonCharTable.Companion.SMALL_E
import JsonCharTable.Companion.SMALL_F
import JsonCharTable.Companion.SMALL_N
import JsonCharTable.Companion.SMALL_T
import JsonCharTable.Companion.numTable
import JsonCharTable.Companion.wsLookUp

/**
 * There is no validation for anything, it assumes that coming [JsonObject] is valid
 * @param str source string of json
 * @param start start index of json elements
 *
 * @return JsonElement
 * */

class JsonObject(
    private val src: String,
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
        val str = this.src

        while(str[ptr] == OPEN_CURLY){
            ptr += 1
        }


        while(true){

            val c = str[ptr]

            when{
                numTable[c.code] || c == DASH->{
                    keyParse = true
                    value = parseNumber()
                    values[key] = value
                }
                c == QUOT ->{
                    if(keyParse){
                        keyParse = false
                        key = parseKey()
                    }else{
                        keyParse = true
                        value = parseString()
                        values[key] = value
                    }

                }
                c == SMALL_T ||  c == SMALL_F || c == SMALL_N -> {
                    keyParse = true
                    value = parseBoolean(c)
                    values[key] = value
                }
                c == OPEN_CURLY -> {

                    keyParse = true
                    value = JsonObject(str, ptr)
                    ptr = value.ptr + 1
                    values[key] = value
                }

                c == OPEN_BRACKET -> {
                    keyParse = true
                    value = JsonArray(str, ptr)
                    ptr = value.ptr + 1
                    values[key] = value
                }

                //c == ','|| c == ' ' || c== ':' || c == '\n' || c =='\t'
                //wsLookUp[c.code]
                wsLookUp[c.code] ->{
                    ptr += 1
                }

                c == CLOSE_CURLY ->{
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
        val str = this.src

        while(true){
            val c: Char = str[ptr]
            //if end of value
            if(c == '\"'){
                ptr += 1
                //return JsonPrimitive(str.substring(start,ptr - 1))
                return JsonPrimitive(str.subSequence(start,ptr - 1).toString())
            }
            if(c == '\\'){
                ptr += 1
            }
            ptr += 1
        }


    }


    private fun parseKey(): String{
        // skip starting of string
        val start = ++ptr
        val str = this.src
        while(true){
            val c = str[ptr]
            //if end of value
            if(c == '\"'){
                ptr += 1
                //return str.substring(start,ptr-1) //13 inst
                return str.subSequence(start,ptr-1).toString() // 10 inst
            }
            if(c == '\\'){
                ptr += 1
            }
            ptr += 1
        }
    }

    /**
     * Returns Double or Long
     *
     * */
    private fun parseNumber(): JsonPrimitive{
        val start = ptr
        val str = this.src
        var dot = false
        while(true){
            val c: Char = str[ptr]
            when{
                numTable[c.code] || c == DASH -> {
                    ptr += 1
                }
                c == DOT || c == SMALL_E || c == CAPITAL_E -> {
                    dot = true
                    ptr += 1
                }
                else -> {
                    /*
                    * Using subSequence() generates less bytecode instructions
                    * */
                    return if(dot){
                        //JsonPrimitive(str.substring(start,ptr).toDouble())
                        JsonPrimitive(str.subSequence(start,ptr).toString().toDouble())
                    }else{

                        //JsonPrimitive(str.substring(start,ptr).toLong())
                        JsonPrimitive(str.subSequence(start,ptr).toString().toLong())
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
        val str = this.src
        while(true){
            val c: Char = str[ptr]
            if(c == COMMA || c == CLOSE_CURLY || c == CLOSE_BRACKET){
                when(firstChar.lowercaseChar()){
                    SMALL_T -> return JsonPrimitive(true)
                    SMALL_F -> return JsonPrimitive(false)
                    SMALL_N -> return JsonPrimitive(null)
                }

            }
            ptr += 1

        }

    }




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