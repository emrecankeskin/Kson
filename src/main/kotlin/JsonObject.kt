
class JsonObject(
    private val str: String,
) {

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

            when(c){
                '0','1','2','3','4','5','6','7','8','9','-' ->{
                    value = parseNumber()
                }
                '"' ->{
                    value = parseString()
                }
                't' , 'f' ,'n' -> {
                    value = parseBoolean()
                }
                '{' -> {
                    val start = ptr
                    moveToEnd('{','}')
                    value = JsonObject(str.substring(start,ptr))
                }

                '[' -> {
                    val start = ptr
                    moveToEnd('[',']')
                    value = JsonArray(str.substring(start,ptr))
                }
                ',',' ','\n','\t','}' ->{
                    ptr++
                    continue
                }
                ']' ->{
                    break
                }
            }
            if(key.isNotEmpty()){
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
            if(c.isDigit() || c == '-' || c =='e' || c == 'E'){
                ptr++
            } else{

                return str.substring(start, ptr).toInt()

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


    fun get(key: String): Any? {
        return values[key]
    }

    inline fun <reified T> getAs(key: String): T? {
        return get(key) as? T
    }
}