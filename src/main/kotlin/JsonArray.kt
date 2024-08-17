

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
        ptr++
        var value: Any

        while(true){

            val c = str[ptr]

            when(c){
                 '0','1','2','3','4','5','6','7','8','9','-' ->{
                     value = parseNumber()
                     list.add(value)
                }
                '"' ->{
                    value = parseString()
                    list.add(value)
                }
                't' , 'f' ,'n' -> {
                    value = parseBoolean()
                }
                '{' -> {
                    val start = ptr
                    moveToEnd('{','}')
                    value = JsonObject(str.substring(start,ptr))
                    list.add(value)

                }

                '[' -> {
                    val start = ptr
                    moveToEnd('[',']')

                    value = JsonArray(str.substring(start,ptr))
                    list.add(value)


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
        }
    }

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



    fun get(index: Int): Any? {
        return if (index in list.indices) list[index] else null
    }

    inline fun <reified T> getAs(index: Int): T? {
        return get(index) as? T
    }
}