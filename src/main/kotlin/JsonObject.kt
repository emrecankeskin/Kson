
class JsonObject(
    private val str: String,
) {

    //TODO after fully implement the library try another ways to represent Json Data
    val values = LinkedHashMap<String,Any>()
    private var ptr = 0

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
            }else if(c == '{'){
                val start = ptr
                while(ptr< str.length){
                    if(str[ptr] == '\\'){
                        ptr++
                    }else if(str[ptr] == '}'){
                        break
                    }
                    ptr++
                }
                value = JsonObject(str.substring(start,ptr))
            }else if(c == '['){
                TODO("Parse Json Array")
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

    private fun parseNumber(): Int{
        val start = ptr
        while(ptr < str.length){
            val c: Char = str[ptr]
            if(c.isDigit()){
                ptr++
            }else{
                return str.substring(start,ptr).toInt()
            }

        }

        return 0
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