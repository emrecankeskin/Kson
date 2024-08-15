
//https://www.microfocus.com/documentation/silk-performer/195/en/silkperformer-195-webhelp-en/GUID-0847DE13-2A2F-44F2-A6E7-214CD703BF84.html

class JsonArray(
    private val str: String,
) {

    val list = ArrayList<Any>()
    private var ptr = 0

    init {
        this.parse()
    }

    private fun parse(){
        skipToStart()
        while(ptr < str.length){
            var value= Any()

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

            ptr++
            list.add(value)
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

    private fun skipToStart(){
        while(ptr < str.length){
            if(str[ptr] == ' '){
                ptr++
            }else if(str[ptr] == '\n'){
                ptr++
            }else if(str[ptr] == '\t'){
                ptr++
            }else if(str[ptr] == '['){
                ptr++
            } else{
                return
            }
        }
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
            if(c.isDigit() || c == '-' || c =='e' || c == 'E'){
                ptr++
            }else{
                //ptr++
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


    fun get(index: Int): Any? {
        return if (index in list.indices) list[index] else null
    }

    inline fun <reified T> getAs(index: Int): T? {
        return get(index) as? T
    }
}