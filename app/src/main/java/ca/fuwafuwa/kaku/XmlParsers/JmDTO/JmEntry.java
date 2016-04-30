package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.JmConsts;

public class JmEntry {

    private static final String JMTAG = JmConsts.ENTRY;
    
    private String ent_seq = null;
    private List<JmKEle> k_ele = new ArrayList<>();
    private List<JmREle> r_ele = new ArrayList<>();
    private JmInfo info = null;
    private List<JmSense> sense = new ArrayList<>();

    public JmEntry(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, JMTAG);
        parser.nextToken();

        while (!JMTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch (name) {
                case JmConsts.ENT_SEQ:
                    ent_seq = CommonParser.parseString(parser);
                    break;
                case JmConsts.K_ELE:
                    k_ele.add(new JmKEle(parser));
                    break;
                case JmConsts.R_ELE:
                    r_ele.add(new JmREle(parser));
                    break;
                case JmConsts.INFO:
                    info = new JmInfo(parser);
                    break;
                case JmConsts.SENSE:
                    sense.add(new JmSense(parser));
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, JMTAG);
    }

    /**
     * A unique numeric sequence number for each entry
     */
    public String getEntSeq(){
        return this.ent_seq;
    }

    public List<JmKEle> getKEle(){
        return this.k_ele;
    }

    public List<JmREle> getREle(){
        return this.r_ele;
    }

    public JmInfo getInfo(){
        return this.info;
    }

    public List<JmSense> getSense(){
        return this.sense;
    }
}
