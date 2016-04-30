package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.JmConsts;

/**
 * general coded information relating to the entry as a whole.
 */
public class JmInfo {

    private static final String JMTAG = JmConsts.INFO;

    private List<JmLinks> links = new ArrayList<>();
    private List<JmBibl> bibl = new ArrayList<>();
    private List<String> etym = new ArrayList<>();
    private List<JmAudit> audit = new ArrayList<>();

    public JmInfo(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, JMTAG);
        parser.nextToken();

        while (!JMTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
                case JmConsts.LINKS:
                    links.add(new JmLinks(parser));
                    break;
                case JmConsts.BIBL:
                    bibl.add(new JmBibl(parser));
                    break;
                case JmConsts.ETYM:
                    etym.add(CommonParser.parseString(parser));
                    break;
                case JmConsts.AUDIT:
                    audit.add(new JmAudit(parser));
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, JMTAG);
    }

    /**
     * This field is used to hold information about the etymology
     * of the kanji or kana parts of the entry. For gairaigo,
     * etymological information may also be in the <lsource> element.
     */
    public List<String> getEtym(){
        return this.etym;
    }

    public List<JmLinks> getLinks(){
        return this.links;
    }

    public List<JmBibl> getBibl(){
        return this.bibl;
    }

    public List<JmAudit> getAudit(){
        return this.audit;
    }
}
