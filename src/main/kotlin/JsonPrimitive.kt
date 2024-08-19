class JsonPrimitive(): JsonElement() {


    @PublishedApi internal  var value: Any? = null

    /**
     * @param str source string
     * */
    constructor(str: String): this(){
        this.value = str
    }

    /**
     * @param num source
     * */
    constructor(num: Number): this(){
        this.value = num
    }

    constructor(bool: Boolean): this(){
        this.value = bool
    }

    constructor(none: Nothing?): this(){
        this.value = null
    }


    /**
     * If @param value is not a String it returns null.
     */
    inline fun <reified T> getAs(): T? {
        return value as? T
    }
}