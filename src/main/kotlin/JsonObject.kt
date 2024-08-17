



/**
 * There is no validation for anything, it assumes that coming json object is valid
 *
 *
 * */

class JsonObject(
    private val str: String,
) {

    val values = LinkedHashMap<String,Any>()

    private var ptr: Int = 0

    init {

        this.parse()
    }

    //Used enums with state-machine like solution, but it was 10 ms slower with 608k char file
    private inline fun parse(){

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
                    val start = ptr
                    moveToEnd('{','}')
                    keyParse = true
                    value = JsonObject(str.substring(start,ptr))
                    values[key] = value
                }

                '[' -> {
                    val start = ptr
                    moveToEnd('[',']')
                    keyParse = true
                    value = JsonArray(str.substring(start,ptr))
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

    // [: 91 ]: 93  { ->123  } 125
    //TODO bottleneck try to find better solution
    private fun moveToEnd(open : Char,close: Char){
        var count = 0

        do{
            val c = str[ptr]
            if(c == open){

                count++
            }else if(c == close){
                count--
            }
            ptr++
        }while(count != 0)


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

        //return false
    }


    fun get(key: String): Any? {
        return values[key]
    }

    inline fun <reified T> getAs(key: String): T? {
        return get(key) as? T
    }
}