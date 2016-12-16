package ca.fuwafuwa.kaku.Search;

import ca.fuwafuwa.kaku.Windows.Views.KanjiCharacterView;

/**
 * Created by Xyresic on 12/16/2016.
 */

public class SearchInfo {

    private final String text;
    private final int textOffset;
    private final KanjiCharacterView kanjiCharacterView;

    public SearchInfo(String text, int textOffset, KanjiCharacterView kanjiCharacterView){
        this.text = text;
        this.textOffset = textOffset;
        this.kanjiCharacterView = kanjiCharacterView;
    }

    public String getText() {
        return text;
    }

    public int getTextOffset() {
        return textOffset;
    }

    public KanjiCharacterView getKanjiCharacterView() {
        return kanjiCharacterView;
    }
}
