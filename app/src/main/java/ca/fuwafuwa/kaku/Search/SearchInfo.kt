package ca.fuwafuwa.kaku.Search

import ca.fuwafuwa.kaku.Windows.Data.DisplayData
import ca.fuwafuwa.kaku.Windows.Data.ISquareChar
import ca.fuwafuwa.kaku.Windows.Views.KanjiCharacterView

/**
 * Created by 0xbad1d3a5 on 12/16/2016.
 */

class SearchInfo(private val displayData: DisplayData, private val squareChar: ISquareChar)
{
    val text: String get() = displayData.text

    val textOffset: Int get() = squareChar.index
}
