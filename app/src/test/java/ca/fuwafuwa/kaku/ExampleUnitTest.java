package ca.fuwafuwa.kaku;

import org.junit.BeforeClass;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;

import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmDict;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @BeforeClass
    public static void ClassSetup(){
        System.setProperty("jdk.xml.entityExpansionLimit", "0");
    }

    @Test
    public void wtfXmlSrsly() throws Exception {
        Serializer serializer = new Persister();
        File file = new File("D:\\Android\\JMDict.xml");
        JmDict dict = serializer.read(JmDict.class, file, false);
    }
}