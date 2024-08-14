
class JsonObject(
    private val str: String,
    private var ptr: Int = 0
) {

    // Implement better type for JsonValues
    val values = LinkedHashMap<String,Any>()

    init {
        this.parse()
    }
    private fun parse(){

        while(ptr < str.length){
            var value: Any = ""
            skipKeyStart()
            val key: String = parseString()
            skipToStart()

            //find better way to check index out of bounds
            if(ptr >= str.length){
                return
            }
            val c = str[ptr]

            if( (c.isDigit() || c == '-')){
                value = parseNumber()
            }else if(c == '"'){
                value = parseString()
            }else if(str[ptr] == 't' || c == 'f' || c == 'n'){
                value = parseBoolean()
            }

            if(key.isNotEmpty()){
                values[key] = value
            }

        }

    }


    private fun skipKeyStart(){
        while(ptr < str.length && str[ptr] != '"') ptr++
    }
    private fun skipToStart(){
        while(ptr < str.length && (str[ptr] == ' ' || str[ptr] == ':')) ptr++
    }

    //TODO check if this is better than returning String?
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

    private fun parseNumber(): String{
        val start = ptr
        while(ptr < str.length){
            val c: Char = str[ptr]
            if(c.isDigit()){
                ptr++
            }else{
                return str.substring(start,ptr)
            }

        }

        return ""
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
}