package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

/**
 * Bibliographic information about the entry. The bib_tag will a
 * coded reference to an entry in an external bibliographic database.
 * The bib_txt field may be used for brief (local) descriptions.
 */
public class JmBibl {

    private String bib_tag;
    private String bib_txt;

    public JmBibl(String bib_tag, String bib_txt) {
        this.bib_tag = bib_tag;
        this.bib_txt = bib_txt;
    }

    public String getBibTag(){
        return this.bib_tag;
    }

    public String getBib_txt(){
        return this.bib_txt;
    }
}
