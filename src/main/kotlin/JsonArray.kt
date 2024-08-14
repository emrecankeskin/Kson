
//https://www.microfocus.com/documentation/silk-performer/195/en/silkperformer-195-webhelp-en/GUID-0847DE13-2A2F-44F2-A6E7-214CD703BF84.html

class JsonArray(
    private val str: String,
)  {

    val values = ArrayList<Any>()
    private var ptr = 0

    init {
        this.parse()
    }

    private fun parse(){
        while(ptr < str.length){
            var value: Any = ""
            skipToStart()
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
                val start = ptr
                moveToEnd(c)
                value = JsonArray(str.substring(start,ptr))
            }
            ptr++
            values.add(value)
        }
    }

    private fun moveToEnd(c : Char){
        while(ptr< str.length){
            if(str[ptr] == '\\'){
                ptr++
            }else if(str[ptr] == c){
                break
            }
            ptr++
        }
    }

    private fun skipToStart(){
        while(ptr < str.length && str[ptr] != '"') ptr++
    }

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