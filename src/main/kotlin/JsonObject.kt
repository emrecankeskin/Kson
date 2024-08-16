
class JsonObject(
    private val str: String,
) {

    val values = LinkedHashMap<String,Any>()
    private var ptr: Int = 0

    init {
        this.parse()
    }

    //Used enums with state-machine like solution, but it was 10 ms slower with 608k char file
    private fun parse(){

        // 1 0 -> 0 0 -> 1 1
        var keyParse = true
        var valParse = false
        var value: Any = ""
        var key  = ""

        while(str[ptr] == '{'){
            ptr++
        }
        //ptr < str.length
        while(ptr < str.length){


            val c = str[ptr]
            when(c){
                '0','1','2','3','4','5','6','7','8','9','-' ->{
                    valParse = true
                    keyParse = true
                    value = parseNumber()
                }
                '"' ->{
                    if(keyParse){
                        keyParse = false
                        valParse = false
                        key = parseString()
                    }else{
                        valParse = true
                        keyParse = true
                        value = parseString()
                    }

                }
                't' , 'f' ,'n' -> {
                    valParse = true
                    keyParse = true
                    value = parseBoolean()
                }
                '{' -> {
                    val start = ptr
                    moveToEnd('{','}')
                    valParse = true
                    keyParse = true
                    value = JsonObject(str.substring(start,ptr))
                }

                '[' -> {
                    val start = ptr
                    moveToEnd('[',']')
                    valParse = true
                    keyParse = true
                    value = JsonArray(str.substring(start,ptr))
                }
                ',',' ',':','\n','\t' ->{
                    ptr++
                    continue
                }
                '}' ->{
                    break
                }
            }
            if(valParse){
                values[key] = value
            }

        }

    }

    private fun moveToEnd(open : Char,close: Char){
        var count = 0
        do{
            if(str[ptr] == '\\'){
                ptr++
            }else if(str[ptr] == open){
                count++
            }else if(str[ptr] == close){
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

        while(ptr < str.length){
            val c: Char = str[ptr]
            //if end of value
            if(c == '\"'){
                ptr++
                return str.substring(start,ptr-1)
            }
            if(c == '\\' && ptr+1 < str.length){

                ptr++
                when(str[ptr]){
                    '\"','/','\\','b','f','r','n','t' -> {
                        //allow to do nothing
                    }
                    else -> {
                        return String()
                    }
                }
            }
            ptr++
        }

        return String()
    }

    private fun parseNumber(): Number{
        val start = ptr
        var dot = false

        while(ptr < str.length){
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
                    break
                }
            }
        }

        return if(dot){
            str.substring(start,ptr).toDouble()
        }else{
            str.substring(start,ptr).toLong()
        }
    }
    private fun parseBoolean(): String{
        val start = ptr
        while(ptr < str.length){
            val c: Char = str[ptr]
            if(c == ',' || c == '}'){
                return str.substring(start,ptr)
            }
            ptr++

        }

        return ""
    }


    fun get(key: String): Any? {
        return values[key]
    }

    inline fun <reified T> getAs(key: String): T? {
        return get(key) as? T
    }
}