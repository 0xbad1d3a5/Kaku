package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.JmConsts;

/**
 * Bibliographic information about the entry. The bib_tag will a
 * coded reference to an entry in an external bibliographic database.
 * The bib_txt field may be used for brief (local) descriptions.
 */
public class JmBibl {

    private static final String JMTAG = JmConsts.BIBL;

    private String bib_tag = null;
    private String bib_txt = null;

    public JmBibl(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, JMTAG);
        parser.nextToken();

        while (!JMTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
                case JmConsts.BIB_TAG:
                    bib_tag = CommonParser.parseString(parser);
                    break;
                case JmConsts.BIB_TXT:
                    bib_txt = CommonParser.parseString(parser);
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, JMTAG);
    }

    public String getBibTag(){
        return this.bib_tag;
    }

    public String getBib_txt(){
        return this.bib_txt;
    }
}
