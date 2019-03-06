package ca.fuwafuwa.kaku

class LangUtils {

    companion object {

        private val KanaHalf: IntArray = intArrayOf(
                0x3092, 0x3041, 0x3043, 0x3045, 0x3047, 0x3049, 0x3083, 0x3085,
                0x3087, 0x3063, 0x30FC, 0x3042, 0x3044, 0x3046, 0x3048, 0x304A,
                0x304B, 0x304D, 0x304F, 0x3051, 0x3053, 0x3055, 0x3057, 0x3059,
                0x305B, 0x305D, 0x305F, 0x3061, 0x3064, 0x3066, 0x3068, 0x306A,
                0x306B, 0x306C, 0x306D, 0x306E, 0x306F, 0x3072, 0x3075, 0x3078,
                0x307B, 0x307E, 0x307F, 0x3080, 0x3081, 0x3082, 0x3084, 0x3086,
                0x3088, 0x3089, 0x308A, 0x308B, 0x308C, 0x308D, 0x308F, 0x3093
        )

        private val KanaVoiced: IntArray = intArrayOf(
                0x30F4, 0xFF74, 0xFF75, 0x304C, 0x304E, 0x3050, 0x3052, 0x3054,
                0x3056, 0x3058, 0x305A, 0x305C, 0x305E, 0x3060, 0x3062, 0x3065,
                0x3067, 0x3069, 0xFF85, 0xFF86, 0xFF87, 0xFF88, 0xFF89, 0x3070,
                0x3073, 0x3076, 0x3079, 0x307C
        )

        private val KanaSemiVoiced: IntArray = intArrayOf(
                0x3071, 0x3074, 0x3077, 0x307A, 0x307D
        )

        fun IsHiragana(char: Char) : Boolean
        {
            return Character.UnicodeBlock.of(char) == Character.UnicodeBlock.HIRAGANA
        }

        fun IsKatakana(char: Char) : Boolean
        {
            return Character.UnicodeBlock.of(char) == Character.UnicodeBlock.KATAKANA
        }

        fun ConvertKanatanaToHiragana(text: String): String
        {
            var result: StringBuilder = StringBuilder()
            var ordPrev: Int = 0;

            for (i in text){

                var ordCurr: Int = i.toInt()

                // Full-width katakana to hiragana
                if ((ordCurr >= 0x30A1) && (ordCurr <= 0x30F3))
                {
                    ordCurr -= 0x60
                }
                // Half-width katakana to hiragana
                else if ((ordCurr >= 0xFF66) && (ordCurr <= 0xFF9D))
                {
                    ordCurr = KanaHalf[ordCurr - 0xFF66]
                }
                // Voiced (used in half-width katakana) to hiragana
                else if (ordCurr == 0xFF9E)
                {
                    if (ordPrev >= 0xFF73 && ordPrev <= 0xFF8E)
                    {
                        result.setLength(result.length - 1)
                        ordCurr = KanaVoiced[ordPrev - 0xFF73]
                    }
                }
                // Semi-voiced (used in half-width katakana) to hiragana
                else if (ordCurr == 0xFF9F)
                {
                    if (ordPrev >= 0xFF8A && ordPrev <= 0xFF8E)
                    {
                        result.setLength(result.length - 1)
                        ordCurr = KanaSemiVoiced[ordPrev - 0xFF8A]
                    }
                }
                // Ignore Japanese ~
                else if (ordCurr == 0xFF5E)
                {
                    ordPrev = 0
                    continue
                }

                result.append(ordCurr.toChar())
                ordPrev = ordCurr
            }

            return result.toString()
        }

        fun ConvertIntToCircledNum(num: Int): String
        {
            var circledNum: String = "($num)"

            if (num == 0)
            {
                circledNum = "⓪"
            } else if ((num >= 1) && (num <= 20))
            {
                circledNum = (('①' - 1) + num).toString()
            }
            // Note: Numbers over 20 may depend on font
            else if ((num >= 21) && (num <= 35))
            {
                circledNum = (('㉑' - 21) + num).toString()
            }
            else if ((num >= 36) && (num <= 50))
            {
                circledNum = (('㊱' - 36) + num).toString()
            }

            return circledNum
        }
    }
}