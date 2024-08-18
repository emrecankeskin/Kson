abstract class JsonElement {



    fun asJsonPrimitive(): JsonPrimitive {
        return this as JsonPrimitive
    }

    fun asJsonObject(): JsonObject{
        return this as JsonObject
    }

    fun asJsonArray(): JsonArray?{
        return this as? JsonArray
    }



}