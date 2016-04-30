package ca.fuwafuwa.kaku.XmlParsers;

import android.content.Context;
import android.util.Log;

import junit.framework.Assert;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmEntry;
import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmInfo;
import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmKEle;
import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmREle;
import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmSense;

/**
 * Created by Xyresic on 4/25/2016.
 */
public class JmDictParser {

    private interface IParse<T> {
        T parse(XmlPullParser parser) throws IOException, XmlPullParserException;
    }

    private static final String TAG = JmDictParser.class.getName();

    private Context mContext;

    FileInputStream jmDictXml;

    public JmDictParser(Context mContext) {

        this.mContext = mContext;
    }

    public void parseDict() throws Exception{

        Log.d(TAG, "INITIALIZING DICTIONARY");

        long startTime = System.currentTimeMillis();

        String fileLoc = mContext.getExternalFilesDir(null).getAbsolutePath();
        File file = new File(fileLoc, "Test.xml");
        Log.d(TAG, file.getAbsolutePath());

        Log.d(TAG, String.format("FINISHED, TOOK %d", System.currentTimeMillis() - startTime));
    }

    public void parse(XmlPullParser parser) throws XmlPullParserException {

        Assert.assertEquals(XmlPullParser.START_TAG, parser.getEventType());

        switch (parser.getName()) {
            case JmDictConstants.ENTRY:
                break;

            case JmDictConstants.ENT_SEQ:
                break;

            case JmDictConstants.K_ELE:
                break;
            case JmDictConstants.KEB:
                break;
            case JmDictConstants.KE_INF:
                break;
            case JmDictConstants.KE_PRI:
                break;

            case JmDictConstants.R_ELE:
                break;
            case JmDictConstants.REB:
                break;
            case JmDictConstants.RE_NOKANJI:
                break;
            case JmDictConstants.RE_RESTR:
                break;
            case JmDictConstants.RE_INF:
                break;
            case JmDictConstants.RE_PRI:
                break;

            case JmDictConstants.INFO:
                break;
            case JmDictConstants.LINKS:
                break;
            case JmDictConstants.LINK_TAG:
                break;
            case JmDictConstants.LINK_DESC:
                break;
            case JmDictConstants.LINK_URI:
                break;
            case JmDictConstants.BIBL:
                break;
            case JmDictConstants.BIB_TAG:
                break;
            case JmDictConstants.BIB_TXT:
                break;
            case JmDictConstants.AUDIT:
                break;
            case JmDictConstants.UPD_DATE:
                break;
            case JmDictConstants.UPD_DETL:
                break;
            case JmDictConstants.ETYM:
                break;

            case JmDictConstants.SENSE:
                break;
            case JmDictConstants.STAGK:
                break;
            case JmDictConstants.STAGR:
                break;
            case JmDictConstants.POS:
                break;
            case JmDictConstants.XREF:
                break;
            case JmDictConstants.ANT:
                break;
            case JmDictConstants.FIELD:
                break;
            case JmDictConstants.MISC:
                break;
            case JmDictConstants.S_INF:
                break;
            case JmDictConstants.LSOURCE:
                break;
            case JmDictConstants.DIAL:
                break;
            case JmDictConstants.GLOSS:
                break;
            case JmDictConstants.EXAMPLE:
                break;
        }
    }

    private JmEntry parseEntry(XmlPullParser parser) throws IOException, XmlPullParserException {
        return parseGeneric(parser, new IParse<JmEntry>() {
            @Override
            public JmEntry parse(XmlPullParser parser) throws IOException, XmlPullParserException {
                String ent_seq = null;
                List<JmKEle> jmKEle = new ArrayList<>();
                List<JmREle> jmREle = new ArrayList<>();
                JmInfo jmInfo = null;
                List<JmSense> jmSense = new ArrayList<>();

                switch (parser.getName()) {
                    case JmDictConstants.ENT_SEQ:
                        ent_seq = parseString(parser);
                        break;
                    case JmDictConstants.K_ELE:
                        break;
                    case JmDictConstants.R_ELE:
                        break;
                    case JmDictConstants.INFO:
                        break;
                    case JmDictConstants.SENSE:
                        break;
                }

                return new JmEntry(ent_seq, jmKEle, jmREle, jmInfo, jmSense);
            }
        });
    }

    private List<JmKEle> parseKEleList(XmlPullParser parser) throws IOException, XmlPullParserException {
        return parseList(parser, new IParse<JmKEle>() {
            @Override
            public JmKEle parse(XmlPullParser parser) throws IOException, XmlPullParserException {

                String keb = null;
                List<String> ke_inf = new ArrayList<String>();
                List<String> ke_pri = new ArrayList<String>();

                switch(parser.getName()){
                    case JmDictConstants.KEB:
                        keb = parseString(parser);
                        break;
                    case JmDictConstants.KE_INF:
                        ke_inf = parseStringList(parser);
                        break;
                    case JmDictConstants.KE_PRI:
                        ke_pri = parseStringList(parser);
                        break;
                }

                return new JmKEle(keb, ke_inf, ke_pri);
            }
        });
    }

    private List<String> parseStringList(XmlPullParser parser) throws IOException, XmlPullParserException {
        return parseList(parser, new IParse<String>() {
            @Override
            public String parse(XmlPullParser parser) throws IOException, XmlPullParserException {
                return parseString(parser);
            }
        });
    }

    private <T> List<T> parseList(XmlPullParser parser, final IParse<T> oneParse) throws IOException, XmlPullParserException {
        return parseGeneric(parser, new IParse<List<T>>() {
            @Override
            public List<T> parse(XmlPullParser parser) throws IOException, XmlPullParserException {
                Assert.assertEquals(XmlPullParser.START_TAG, parser.getEventType());
                String tagName = parser.getName();

                List<T> list = new ArrayList<>();

                while (tagName.equals(parser.getName())){
                    list.add(parseGeneric(parser, oneParse));
                }

                return list;
            }
        });
    }

    private String parseString(XmlPullParser parser) throws IOException, XmlPullParserException {
        return parseGeneric(parser, new IParse<String>() {
            @Override
            public String parse(XmlPullParser parser) throws IOException, XmlPullParserException {
                Assert.assertEquals(XmlPullParser.TEXT, parser.nextToken());
                return parser.getText();
            }
        });
    }

    private <T> T parseGeneric(XmlPullParser parser, IParse<T> func) throws IOException, XmlPullParserException {
        Assert.assertEquals(XmlPullParser.START_TAG, parser.getEventType());
        String tagName = parser.getName();

        T returnType = func.parse(parser);

        Assert.assertEquals(XmlPullParser.END_TAG, parser.nextToken());
        Assert.assertEquals(tagName, parser.getName());
        parser.nextToken();

        return returnType;
    }
}
