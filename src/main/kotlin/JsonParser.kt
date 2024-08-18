class JsonParser{


    companion object{
        val wsLookUp = BooleanArray(256).apply {

            //white space
            this[9] = true
            this[10] = true
            this[32] = true
            this[44] = true

            this[58] = true
        }
//        val digitLookUp = BooleanArray(256).apply{
//            this[45] = true
//            this[48] = true
//            this[49] = true
//            this[50] = true
//            this[51] = true
//            this[52] = true
//            this[53] = true
//            this[54] = true
//            this[55] = true
//            this[56] = true
//            this[57] = true
//        }

        fun parseFromString(src: String): JsonElement{
            var ptr = 0
            while(ptr < src.length){
                if(src[ptr] == '{'){
                    return JsonObject(src)
                }else if(src[ptr] == '[')
                    return JsonArray(src)

                ptr++
            }

            return JsonPrimitive(-1)
        }



        //TODO? map object
    }
}