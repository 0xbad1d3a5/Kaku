package ca.fuwafuwa.kaku.Windows

import android.content.Context
import ca.fuwafuwa.kaku.*

/**
 * It seems like opening and closing a bunch of windows causes Android to start to lag pretty hard.
 * Therefore, we should keep only one instance of each type of window in memory, and show()ing and
 * hide()ing the window when necessary. This class is to help facilitate this communication.
 *
 * Edit: The lag actually might have been caused by a memory leak. But this is here now, so might
 * as well keep it.
 */
class WindowCoordinator(private val context: Context)
{
    val windows: MutableMap<String, Window> = mutableMapOf()

    private val windowInitMap: Map<String, () -> Window> = mutableMapOf(
            WINDOW_INFO to fun(): Window { return InformationWindow(context, this) },
            WINDOW_EDIT to fun(): Window { return EditWindow(context, this) },
            WINDOW_CAPTURE to fun(): Window { return CaptureWindow(context, this) },
            WINDOW_INSTANT_KANJI to fun(): Window { return InstantKanjiWindow(context, this) },
            WINDOW_KANJI_CHOICE to fun(): Window { return KanjiChoiceWindow(context, this) }
    )

    fun getWindow(key: String) : Window
    {
        if (!windows.containsKey(key))
        {
            windows[key] = windowInitMap[key]!!.invoke()
        }

        return windows[key]!!
    }

    // WARNING: DO NOT USE THIS UNLESS YOU KNOW WHAT YOU ARE DOING
    fun setWindow(key: String, window: Window)
    {
        windows[key] = window
    }

    fun reinitAllWindows()
    {
        windows.forEach { it.value.reInit(Window.ReinitOptions()) }
    }

    fun stopAllWindows()
    {
        windows.forEach { it.value.stop() }
    }
}