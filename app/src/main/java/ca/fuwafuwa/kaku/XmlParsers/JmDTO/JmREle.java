package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.JmConsts;

/**
 * The reading element typically contains the valid readings
 * of the word(s) in the kanji element using modern kanadzukai.
 * Where there are multiple reading elements, they will typically be
 * alternative readings of the kanji element. In the absence of a
 * kanji element, i.e. in the case of a word or phrase written
 * entirely in kana, these elements will define the entry.
 */
public class JmREle {

    private static final String JMTAG = JmConsts.R_ELE;

    private String reb = null;
    private String re_nokanji = null;
    private List<String> re_restr = new ArrayList<>();
    private List<String> re_inf = new ArrayList<>();
    private List<String> re_pri = new ArrayList<>();

    public JmREle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, JMTAG);
        parser.nextToken();

        while (!JMTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
                case JmConsts.REB:
                    reb = CommonParser.parseString(parser);
                    break;
                case JmConsts.RE_NOKANJI:
                    re_nokanji = CommonParser.parseString(parser);
                    break;
                case JmConsts.RE_RESTR:
                    re_restr.add(CommonParser.parseString(parser));
                    break;
                case JmConsts.RE_INF:
                    re_inf.add(CommonParser.parseString(parser));
                    break;
                case JmConsts.RE_PRI:
                    re_pri.add(CommonParser.parseString(parser));
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, JMTAG);
    }

    /**
     * this element content is restricted to kana and related
     * characters such as chouon and kurikaeshi. Kana usage will be
     * consistent between the keb and reb elements; e.g. if the keb
     * contains katakana, so too will the reb.
     */
    public String getReb(){
        return this.reb;
    }

    /**
     * This element, which will usually have a null value, indicates
     * that the reb, while associated with the keb, cannot be regarded
     * as a true reading of the kanji. It is typically used for words
     * such as foreign place names, gairaigo which can be in kanji or
     * katakana, etc.
     */
    public String getReNoKanji(){
        return this.re_nokanji;
    }

    /**
     * This element is used to indicate when the reading only applies
     * to a subset of the keb elements in the entry. In its absence, all
     * readings apply to all kanji elements. The contents of this element
     * must exactly match those of one of the keb elements.
     */
    public List<String> getReRestr(){
        return this.re_restr;
    }

    /**
     * General coded information pertaining to the specific reading.
     * Typically it will be used to indicate some unusual aspect of
     * the reading.
     */
    public List<String> getReInf(){
        return this.re_inf;
    }

    /**
     * See the comment on ke_pri above.
     */
    public List<String> getRePri(){
        return this.re_pri;
    }
}
