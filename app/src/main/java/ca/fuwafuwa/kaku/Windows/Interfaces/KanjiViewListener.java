package ca.fuwafuwa.kaku.Windows.Interfaces;

import android.view.MotionEvent;

import ca.fuwafuwa.kaku.Windows.Views.KanjiCharacterView;

/**
 * Created by 0x1bad1d3a on 5/6/2016.
 */
public interface KanjiViewListener {
    void onKanjiViewTouch(KanjiCharacterView kanjiView, MotionEvent e);
}
