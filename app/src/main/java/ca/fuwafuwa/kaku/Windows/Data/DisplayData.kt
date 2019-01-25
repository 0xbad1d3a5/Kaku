package ca.fuwafuwa.kaku.Windows.Data

import android.graphics.Bitmap
import ca.fuwafuwa.kaku.Ocr.BoxParams
import ca.fuwafuwa.kaku.splitTextByChar

open class DisplayData(var squareChars: List<ISquareChar>)
{
    val text: String
        get()
        {
            val sb = StringBuilder()

            for (char in squareChars)
            {
                sb.append(char.char)
            }

            return sb.toString()
        }

    val count: Int
        get()
        {
            return squareChars.count()
        }

    open var instantMode = false

    fun recomputeChars()
    {
        val newSquareChars = mutableListOf<ISquareChar>()

        for (squareChar in squareChars)
        {
            val newChars = squareChar.text ?: squareChar.char
            squareChar.text = null

            when
            {
                newChars.length > 1 ->
                {
                    val newCharsList = splitTextByChar(newChars)

                    for (newChar in newCharsList)
                    {
                        val newSquareChar = squareChar.clone()
                        newSquareChar.char = newChar
                        if (newSquareChar is SquareCharOcr) newSquareChar.addChoice(newChar, ChoiceCertainty.CERTAIN)
                        newSquareChars.add(newSquareChar)
                    }
                }
                newChars.length == 1 ->
                {
                    squareChar.char = newChars
                    newSquareChars.add(squareChar)
                }
                newChars.length == 0 ->
                {
                    // character was deleted
                }
            }
        }

        squareChars = newSquareChars
        assignIndicies()
    }

    fun assignIndicies()
    {
        for ((index, squareChars) in squareChars.withIndex())
        {
            squareChars.index = index
        }
    }
}

class DisplayDataOcr(val bitmap: Bitmap,
                     val boxParams: BoxParams,
                     override var instantMode: Boolean,
                     squareChars: List<SquareCharOcr>) : DisplayData(squareChars)
{
}