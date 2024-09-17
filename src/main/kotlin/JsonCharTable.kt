class JsonCharTable {

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
        //0-9 digits
        val numTable = BooleanArray(256).apply {
            this[47] = true
            this[48] = true
            this[49] = true
            this[50] = true
            this[51] = true
            this[52] = true
            this[53] = true
            this[54] = true
            this[55] = true
            this[56] = true
            this[57] = true
        }


        const val OPEN_CURLY = '{'
        const val CLOSE_CURLY = '}'

        const val OPEN_BRACKET = '['
        const val CLOSE_BRACKET = ']'

        const val CAPITAL_E = 'E'
        const val SMALL_E = 'e'

        const val SMALL_T = 't'
        const val SMALL_F = 'f'
        const val SMALL_N = 'n'

        const val DOT = '.'
        const val COMMA = ','

        const val QUOT = '"'
        const val DASH = '-'

    }
}