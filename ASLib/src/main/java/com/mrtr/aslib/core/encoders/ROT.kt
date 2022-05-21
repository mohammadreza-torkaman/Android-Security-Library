package com.mrtr.aslib.core.encoders


class ROT {
    companion object {

        private const val LOWER_A_Z_TYPE = 1
        private const val UPPER_A_Z_TYPE = 2
        private const val NOT_LETTER_TYPE = 0

        private const val LOWER_Z_CHAR = 'z'
        private const val LOWER_A_CHAR = 'a'
        private const val UPPER_Z_CHAR = 'Z'
        private const val UPPER_A_CHAR = 'A'
        private const val ZERO_CHAR = '0'
        private const val NINE_CHAR = '9'

        public fun encode(input: String, n: Int, includeDigits: Boolean = false): String {
            val builder = StringBuilder()
            for (element in input) {
                val c: Char = element
                builder.append(rotate(c, n, includeDigits))
            }
            return builder.toString()
        }

        public fun decode(input: String, n: Int, includeDigits: Boolean = false): String {
            val builder = StringBuilder()
            for (element in input) {
                val c: Char = element
                builder.append(unRotate(c, n, includeDigits))
            }
            return builder.toString()
        }

        private fun rotate(current: Char, N: Int, rotateDigits: Boolean): Char {
            var rotated: Char
            val charType = getCharType(current)
            when {

                charType != NOT_LETTER_TYPE -> {
                    rotated = current + N
                    if (charType == LOWER_A_Z_TYPE && rotated > LOWER_Z_CHAR || charType == UPPER_A_Z_TYPE && rotated > UPPER_Z_CHAR)
                        rotated -= LOWER_Z_CHAR - LOWER_A_CHAR + 1
                }

                rotateDigits && isNumeric(current) -> {
                    rotated = current + N % 10
                    if (rotated > NINE_CHAR)
                        rotated -= NINE_CHAR - ZERO_CHAR + 1
                }

                else -> {
                    rotated = current
                }

            }

            return rotated

        }

        private fun unRotate(current: Char, N: Int, rotateDigits: Boolean): Char {
            var rotated: Char
            val charType = getCharType(current)
            when {
                (charType != NOT_LETTER_TYPE) -> {
                    rotated = current - N
                    if (charType == LOWER_A_Z_TYPE && rotated < LOWER_A_CHAR || charType == UPPER_A_Z_TYPE && rotated < UPPER_A_CHAR)
                        rotated += LOWER_Z_CHAR - LOWER_A_CHAR + 1
                }
                (rotateDigits && isNumeric(current)) -> {
                    rotated = current - N % 10
                    if (rotated < ZERO_CHAR)
                        rotated += NINE_CHAR - ZERO_CHAR + 1
                }
                else -> {
                    rotated = current
                }
            }

            return rotated

        }

        private fun getCharType(c: Char): Int {
            return when {
                (c >= LOWER_A_CHAR && c <= LOWER_Z_CHAR) -> LOWER_A_Z_TYPE
                (c >= UPPER_A_CHAR && c <= UPPER_Z_CHAR) -> UPPER_A_Z_TYPE
                else -> NOT_LETTER_TYPE
            }
        }

        private fun isNumeric(c: Char): Boolean {
            return c >= ZERO_CHAR && c <= NINE_CHAR
        }

    }
}