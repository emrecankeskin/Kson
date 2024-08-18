class JsonPrimitive(): JsonElement() {


    @PublishedApi internal lateinit var value: Any


    constructor(str: String): this(){
        this.value = str
    }

    constructor(num: Number): this(){
        this.value = num
    }

    constructor(bool: Boolean): this(){
        this.value = bool
    }


    /**
     * If @param value is not a String it returns null.
     */
    inline fun <reified T> getAs(): T? {
        return value as? T
    }
}