class JsonParser{

    //TODO create buffer for jsonobjects and only store start and end index of values

    /**
     * static method of parser [parseFromString] with [wsLookUp] which is a table for checking if char is whitespace or not
     * */
    companion object{
        val wsLookUp = BooleanArray(256).apply {

            //white space
            this[9] = true
            this[10] = true
            this[32] = true
            this[44] = true
            this[58] = true
        }

        fun parseFromString(src: String): JsonElement{
            var ptr = 0
            while(wsLookUp[src[ptr].code]){
                ptr++
            }

            if(src[ptr] == '['){
                return JsonArray(src)
            }

            return JsonObject(src)
        }



        //TODO? map object
    }
}