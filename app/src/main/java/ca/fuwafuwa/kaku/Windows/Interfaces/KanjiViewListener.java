package ca.fuwafuwa.kaku.Windows.Interfaces;

import android.view.MotionEvent;

import ca.fuwafuwa.kaku.Windows.Views.KanjiCharacterView;

/**
 * Created by 0x1bad1d3a on 5/6/2016.
 */
public interface KanjiViewListener {
    void onKanjiViewScrollStart(KanjiCharacterView kanjiView, MotionEvent e);
    void onKanjiViewScroll(KanjiCharacterView kanjiView, MotionEvent oe, MotionEvent e);
    void onKanjiViewScrollEnd(KanjiCharacterView kanjiView, MotionEvent e);
    void onKanjiViewTouch(KanjiCharacterView kanjiView);
}
