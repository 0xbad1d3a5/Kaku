package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.JmConsts;

/**
 * This element holds details of linking information to
 * entries in other electronic repositories. The link_tag will be
 * coded to indicate the type of link (text, image, sound), the
 * link_desc will provided a textual label for the link, and the
 * link_uri contains the actual URI.
 */
public class JmLinks {

    private static final String JMTAG = JmConsts.LINKS;

    private String link_tag = null;
    private String link_desc = null;
    private String link_uri = null;

    public JmLinks(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, JMTAG);
        parser.nextToken();

        while (!JMTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
                case JmConsts.LINK_TAG:
                    link_tag = CommonParser.parseString(parser);
                    break;
                case JmConsts.LINK_DESC:
                    link_desc = CommonParser.parseString(parser);
                    break;
                case JmConsts.LINK_URI:
                    link_uri = CommonParser.parseString(parser);
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, JMTAG);
    }

    public String getLinkTag(){
        return this.link_tag;
    }

    public String getLinkDesc(){
        return this.link_desc;
    }

    public String getLinkUri(){
        return this.link_uri;
    }
}
