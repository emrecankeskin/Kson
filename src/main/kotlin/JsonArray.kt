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
import java.util.*

//https://www.microfocus.com/documentation/silk-performer/195/en/silkperformer-195-webhelp-en/GUID-0847DE13-2A2F-44F2-A6E7-214CD703BF84.html



class JsonArray(
    private val src: String,
    private val start: Int = 0
): JsonElement(),Iterable<JsonElement>{


    private val list = LinkedList<JsonElement>()
    @PublishedApi internal var ptr = 0

    init {
        this.ptr = start
        this.parse()
    }



    private fun parse(){
        ptr += 1
        var value: JsonElement
        val str = this.src

        while(true){

            val c = str[ptr]
            when{

                numTable[c.code] || c == DASH ->{
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
        val str = this.src

        while(true){
            val c: Char = str[ptr]
            //if end of value
            if(c == '\"'){
                ptr += 1
                return JsonPrimitive(str.subSequence(start,ptr - 1).toString())
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