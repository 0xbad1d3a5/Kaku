package ca.fuwafuwa.kaku;

import org.junit.BeforeClass;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @BeforeClass
    public static void ClassSetup(){
        System.setProperty("jdk.xml.entityExpansionLimit", "0");
    }
}