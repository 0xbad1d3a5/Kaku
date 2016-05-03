package ca.fuwafuwa.kaku.XmlParsers;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.google.common.base.Joiner;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ca.fuwafuwa.kaku.Database.DbOpenHelper;
import ca.fuwafuwa.kaku.Database.Models.Entry;
import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmDict;
import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmEntry;
import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmKEle;

/**
 * Created by Xyresic on 4/30/2016.
 */
public class JmDictThread implements Runnable {

    private static final String TAG = JmDictThread.class.getName();

    private Context mContext;
    FileInputStream jmDictXml;
    private XmlPullParser parser;
    private JmDict dict;

    public JmDictThread(Context context) {
        mContext = context;
    }

    @Override
    public void run() {

        long startTime = System.currentTimeMillis();

        try {
            parser = Xml.newPullParser();
            File file = new File(mContext.getExternalFilesDir(null), "JMDictOriginal.xml");
            jmDictXml = new FileInputStream(file);

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(jmDictXml, null);

            while (!JmConsts.JMDICT.equals(parser.getName())){
                parser.nextToken();
            }
            dict = new JmDict(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, String.format("FINISHED DICT, TOOK %d", System.currentTimeMillis() - startTime));

        DbOpenHelper db = new DbOpenHelper(mContext);

        for (JmEntry jmEntry : dict.getEntries()){
            for (JmKEle kEle : jmEntry.getKEle()){
                Entry entry = new Entry();
                entry.setKanji(kEle.getKeb());
                entry.setReading(Joiner.on("\n").join(jmEntry.getREle()));
                entry.setSense(Joiner.on("\n").join(jmEntry.getSense()));
                db.createEntry(entry);
            }
        }
    }
}
