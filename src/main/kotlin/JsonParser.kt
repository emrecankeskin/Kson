class JsonParser{


    /**
     * static method of parser [parseFromString] with [wsLookUp] which is a table for checking if char is whitespace or not
     * */
    companion object{


        val wsLookUp = BooleanArray(256).apply {

            /**
             * white space chars
            * */
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

            /**
             * if starts with array instead of object
             * */
            if(src[ptr] == '['){
                return JsonArray(src)
            }

            return JsonObject(src)
        }



        //TODO? map object
    }
}