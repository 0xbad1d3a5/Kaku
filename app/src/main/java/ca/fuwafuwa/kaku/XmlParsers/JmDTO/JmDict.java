package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.XmlParsers.JmConsts;

public class JmDict {

    private static final String JMTAG = JmConsts.JMDICT;

    private List<JmEntry> entries = new ArrayList<>();

    public JmDict(XmlPullParser parser, Context mContext) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, JMTAG);
        parser.nextToken();

        while (!JMTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
                case JmConsts.ENTRY:
                    new JmEntry(parser, mContext);
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, JMTAG);
    }

    public List<JmEntry> getEntries(){
        return this.entries;
    }
}
