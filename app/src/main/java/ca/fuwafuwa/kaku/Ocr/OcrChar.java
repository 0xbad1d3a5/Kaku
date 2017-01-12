package ca.fuwafuwa.kaku.Ocr;

import android.util.Pair;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Xyresic on 1/11/2017.
 */

public class OcrChar {

    private List<Pair<String, Double>> mChoices;
    private int[] mPos;

    public OcrChar(List<Pair<String, Double>> choices, int[] pos){

        this.mChoices = choices;
        this.mPos = pos;
        sortChoices();
    }

    public String getBestChoice(){
        return mChoices.get(0).first;
    }

    public List<Pair<String, Double>> getAllChoices(){
        return mChoices;
    }

    public int[] getPos(){
        return mPos;
    }

    private void sortChoices(){

        Collections.sort(mChoices, new Comparator<Pair<String, Double>>() {
            @Override
            public int compare(Pair<String, Double> lhs, Pair<String, Double> rhs) {
                if (lhs.second > rhs.second){
                    return -1;
                }
                else if (lhs.second == rhs.second){
                    return 0;
                }
                else {
                    return 1;
                }
            }
        });
    }
}
