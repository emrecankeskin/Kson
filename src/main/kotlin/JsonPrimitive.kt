class JsonPrimitive(): JsonElement() {


    //TODO implement look up table for checking element type


    var value: Any? = null

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

    /**
     * @param bool source
     * */
    constructor(bool: Boolean): this(){
        this.value = bool
    }


    /**
     * @param none source representing null for JSON
     * */
    constructor(none: Nothing?): this(){
        this.value = null
    }

    override fun toString(): String {
        return value.toString()
    }

    /**
     * If value is not instance of T it will return null
     */

}