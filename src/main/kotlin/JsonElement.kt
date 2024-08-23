abstract class JsonElement {




    fun asJsonPrimitive(): JsonPrimitive {
        return this as JsonPrimitive
    }

    fun asJsonObject(): JsonObject{
        return this as JsonObject
    }

    fun asJsonArray(): JsonArray{
        return this as JsonArray
    }


    fun isJsonPrimitive(): Boolean {
        return this is JsonPrimitive
    }

    fun isJsonObject(): Boolean{
        return this is JsonObject
    }

    fun isJsonArray(): Boolean{
        return this is JsonArray
    }



}