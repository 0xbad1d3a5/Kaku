package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Bibliographic information about the entry. The bib_tag will a
 * coded reference to an entry in an external bibliographic database.
 * The bib_txt field may be used for brief (local) descriptions.
 */
@Root(name = "bibl")
public class Bibl {

    @Element(name = "bib_tag", required = false)
    private String bib_tag;
    @Element(name = "bib_txt", required = false)
    private String bib_txt;

    public String getBibTag(){
        return this.bib_tag;
    }

    public String getBib_txt(){
        return this.bib_txt;
    }
}
