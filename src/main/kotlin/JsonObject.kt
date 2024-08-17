/**
 * There is no validation for anything, it assumes that coming json object is valid
 *
 *
 * */

class JsonObject(
    private val str: String,
    internal val start: Int = 0
) {

    val values = LinkedHashMap<String,Any>()
    internal var ptr: Int



    init {
        this.ptr = start
        this.parse()
    }

    //Used enums with state-machine like solution, but it was 10 ms slower with 608k char file
    private fun parse(){

        // 1 0 -> 0 0 -> 1 1
        var keyParse = true
        var value: Any = Any()
        var key  = ""

        while(str[ptr] == '{'){
            ptr++
        }
        //ptr < str.length
        while(true){

            val c = str[ptr]
            when(c){
                '0','1','2','3','4','5','6','7','8','9','-' ->{
                    keyParse = true
                    value = parseNumber()
                    values[key] = value
                }
                '"' ->{
                    if(keyParse){
                        keyParse = false
                        key = parseString()
                    }else{
                        keyParse = true
                        value = parseString()
                        values[key] = value
                    }

                }
                't' , 'f' ,'n' -> {
                    keyParse = true
                    value = parseBoolean()
                    values[key] = value
                }
                '{' -> {

                    keyParse = true
                    value = JsonObject(str, ptr)
                    ptr = value.ptr + 1
                    values[key] = value
                }

                '[' -> {
                    keyParse = true
                    value = JsonArray(str, ptr)
                    ptr = value.ptr + 1
                    values[key] = value
                }
                ',',' ',':','\n','\t' ->{
                    ptr++
                    continue
                }
                '}' ->{
                    break
                }
            }

        }

    }


    //TODO check if this is better than returning String?
    //TODO can i build this with stringbuilder and pass escape chars
    private fun parseString(): String{
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
    private fun parseNumber(): Number{
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
                        str.substring(start,ptr).toDouble()
                    }else{
                        str.substring(start,ptr).toLong()
                    }
                }
            }
        }

    }


    private fun parseBoolean(): Boolean{
        val start = ptr
        while(true){
            val c: Char = str[ptr]
            if(c == ',' || c == '}'){
                return str.substring(start,ptr).toBoolean()
            }
            ptr++

        }

    }


    fun get(key: String): Any? {
        return values[key]
    }

    inline fun <reified T> getAs(key: String): T? {
        return get(key) as? T
    }
}