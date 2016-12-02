package ca.fuwafuwa.kaku.XmlParsers.JmDict.JmDTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.JmDict.JmConsts;

/**
 * Created by Xyresic on 4/25/2016.
 */

/**
 * The kanji element, or in its absence, the reading element, is
 * the defining component of each entry.
 * The overwhelming majority of entries will have a single kanji
 * element associated with a word in Japanese. Where there are
 * multiple kanji elements within an entry, they will be orthographical
 * variants of the same word, either using variations in okurigana, or
 * alternative and equivalent kanji. Common "mis-spellings" may be
 * included, provided they are associated with appropriate information
 * fields. Synonyms are not included; they may be indicated in the
 * cross-reference field associated with the sense element.
 */
public class JmKEle {

    private static final String XMLTAG = JmConsts.K_ELE;

    private String keb = null;
    private List<String> ke_inf = new ArrayList<>();
    private List<String> ke_pri = new ArrayList<>();

    public JmKEle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);
        parser.nextToken();

        while (!XMLTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
                case JmConsts.KEB:
                    keb = CommonParser.parseString(parser);
                    break;
                case JmConsts.KE_INF:
                    ke_inf.add(CommonParser.parseString(parser));
                    break;
                case JmConsts.KE_PRI:
                    ke_pri.add(CommonParser.parseString(parser));
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, XMLTAG);
    }

    /**
     * This element will contain a word or short phrase in Japanese
     * which is written using at least one non-kana character (usually kanji,
     * but can be other characters). The valid characters are
     * kanji, kana, related characters such as chouon and kurikaeshi, and
     * in exceptional cases, letters from other alphabets.
     */
    public String getKeb(){
        return this.keb;
    }

    /**
     * This is a coded information field related specifically to the
     * orthography of the keb, and will typically indicate some unusual
     * aspect, such as okurigana irregularity.
     */
    public List<String> getKeInf(){
        return this.ke_inf;
    }

    /**
     * This and the equivalent re_pri field are provided to record
     * information about the relative priority of the entry,  and consist
     * of codes indicating the word appears in various references which
     * can be taken as an indication of the frequency with which the word
     * is used. This field is intended for use either by applications which
     * want to concentrate on entries of  a particular priority, or to
     * generate subset files.
     * The current values in this field are:
     * - news1/2: appears in the "wordfreq" file compiled by Alexandre Girardi
     * from the Mainichi Shimbun. (See the Monash ftp archive for a copy.)
     * Words in the first 12,000 in that file are marked "news1" and words
     * in the second 12,000 are marked "news2".
     * - ichi1/2: appears in the "Ichimango goi bunruishuu", Senmon Kyouiku
     * Publishing, Tokyo, 1998.  (The entries marked "ichi2" were
     * demoted from ichi1 because they were observed to have low
     * frequencies in the WWW and newspapers.)
     * - spec1 and spec2: a small number of words use this marker when they
     * are detected as being common, but are not included in other lists.
     * - gai1/2: common loanwords, based on the wordfreq file.
     * - nfxx: this is an indicator of frequency-of-use ranking in the
     * wordfreq file. "xx" is the number of the set of 500 words in which
     * the entry can be found, with "01" assigned to the first 500, "02"
     * to the second, and so on. (The entries with news1, ichi1, spec1 and
     * gai1 values are marked with a "(P)" in the EDICT and EDICT2
     * files.)
     * <p/>
     * The reason both the kanji and reading elements are tagged is because
     * on occasions a priority is only associated with a particular
     * kanji/reading pair.
     */
    public List<String> getKePri(){
        return this.ke_pri;
    }
}

