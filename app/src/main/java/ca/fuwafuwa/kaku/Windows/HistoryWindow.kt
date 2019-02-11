package ca.fuwafuwa.kaku.Windows

import android.content.Context
import ca.fuwafuwa.kaku.R
import ca.fuwafuwa.kaku.Search.JmSearchResult
import ca.fuwafuwa.kaku.Search.SearchInfo
import ca.fuwafuwa.kaku.Search.Searcher
import ca.fuwafuwa.kaku.Windows.Data.ISquareChar
import ca.fuwafuwa.kaku.Windows.Interfaces.ISearchPerformer

class HistoryWindow(context: Context,
                    windowCoordinator: WindowCoordinator) : Window(context, windowCoordinator, R.layout.window_instant_info), Searcher.SearchDictDone, ISearchPerformer
{
    override fun jmResultsCallback(results: MutableList<JmSearchResult>?, search: SearchInfo?)
    {
    }

    override fun performSearch(squareChar: ISquareChar)
    {
    }
}