package ca.fuwafuwa.kaku.Database.Models;

/**
 * Created by Xyresic on 5/2/2016.
 */
public class Entry {

    private String kanji;
    private String reading;
    private String sense;

    public Entry(){
    }

    public Entry(String kanji, String reading, String sense){
        this.kanji = kanji;
        this.reading = reading;
        this.sense = sense;
    }

    public String getKanji() {
        return kanji;
    }

    public String getReading() {
        return reading;
    }

    public String getSense() {
        return sense;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public void setSense(String sense) {
        this.sense = sense;
    }

    public String toString(){
        if (kanji != null){
            return String.format("Kanji: %s\nReading: %s\nSense: %s\n", kanji, reading, sense);
        }
        else {
            return null;
        }
    }
}
