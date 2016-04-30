package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.JmConsts;

/**
 * The audit element will contain the date and other information
 * about updates to the entry. Can be used to record the source of
 * the material.
 */
public class JmAudit {

    private static final String JMTAG = JmConsts.AUDIT;

    private String upd_date = null;
    private String upd_detl = null;

    public JmAudit(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, JMTAG);
        parser.nextToken();

        while (!JMTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
                case JmConsts.UPD_DATE:
                    upd_date = CommonParser.parseString(parser);
                    break;
                case JmConsts.UPD_DETL:
                    upd_detl = CommonParser.parseString(parser);
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, JMTAG);
    }

    public String getUpdDate(){
        return this.upd_date;
    }

    public String getUpdDetl(){
        return this.upd_detl;
    }
}
