package ca.fuwafuwa.kaku.Search;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Entry;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Meaning;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningGloss;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Reading;

/**
 * Created by Xyresic on 11/29/2016.
 */

public class Match implements Comparable<Match> {

    private static final String TAG = Match.class.getName();

    private String mKanji;
    private List<String> mResultReadings = new ArrayList<>();
    private List<String> mResultMeanings = new ArrayList<>();

    private Entry mEntry;
    private List<Reading> mReadings;
    private List<Meaning> mMeanings;
    private int mCharsMatched;

    public Match(String kanji, Entry entry, int charsMatched) {
        this.mKanji = kanji;
        this.mCharsMatched = charsMatched;
        this.mEntry = entry;

        populateData();
    }

    public String getKanji() {
        return mKanji;
    }

    public int getCharsMatched() {
        return mCharsMatched;
    }

    private void populateData(){
        //Log.d(TAG, mEntry.toString());
        populateReadingData();
        populateMeaningData();
    }

    private void populateReadingData(){
        for (Reading r : mEntry.getReadings()){
            if (!r.getReadingRestrictions().isEmpty() && r.getReadingRestrictions().contains(mKanji)){
                mResultReadings.add(r.getReading());
            }
            else if (r.getReadingRestrictions().isEmpty()) {
                mResultReadings.add(r.getReading());
            }
        }
    }

    private void populateMeaningData(){
        for (Meaning m : mEntry.getMeanings()){
            if (!m.getKanjiRestrictions().isEmpty() && m.getKanjiRestrictions().contains(mKanji)){
                addGloss(m);
            }
            else if (m.getKanjiRestrictions().isEmpty()){
                addGloss(m);
            }
        }
    }

    private void addGloss(Meaning m){
        for (MeaningGloss gloss : m.getGlosses()){
            mResultMeanings.add(gloss.getGloss());
        }
    }

    @Override
    public String toString(){

        StringBuilder sb = new StringBuilder();

        sb.append(mKanji);

        sb.append(" (");
        sb.append(Joiner.on(", ").join(mResultReadings));
        sb.append(")");

        sb.append("\n");
        sb.append(Joiner.on(", ").join(mResultMeanings));

        return sb.toString();
    }

    @Override
    public int compareTo(Match another) {
        if (this.getCharsMatched() > another.getCharsMatched()){
            return -1;
        }
        else if (this.getCharsMatched() == another.getCharsMatched()){
            return 0;
        }
        else {
            return 1;
        }
    }
}
