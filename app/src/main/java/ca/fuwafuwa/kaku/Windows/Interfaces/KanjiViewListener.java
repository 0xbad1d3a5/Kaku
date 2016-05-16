package ca.fuwafuwa.kaku.Windows.Interfaces;

import android.view.MotionEvent;

import ca.fuwafuwa.kaku.Windows.Views.KanjiCharacterView;

/**
 * Created by Xyresic on 5/6/2016.
 */
public interface KanjiViewListener {
    void onKanjiViewScroll(KanjiCharacterView kanjiView, MotionEvent e);
    void onKanjiViewTouch(KanjiCharacterView kanjiView, MotionEvent e);
}
