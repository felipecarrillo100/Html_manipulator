package org.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Set;

/**
 * Unit test for simple App.
 */
public class AppTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public void testHTML_Step_CanCompare() {
        String NAME = "STEP_01";
        String ACTION = "REPLACE_WITH";
        String HTML_TARGET_MATCH = "nav#navbar";
        String HTML_REPLACEMENT = "./platform/samples/navbar.html";
        String SUBSTR = null;
        String ATTR = null;
        String VALUE = null;
        String WHICH = "FIRST";
        final HTML_Step aStep1 = new HTML_Step(NAME, ACTION, HTML_TARGET_MATCH, HTML_REPLACEMENT, SUBSTR, ATTR, VALUE, WHICH);
        final HTML_Step aStep2 = new HTML_Step("STEP_02", ACTION, HTML_TARGET_MATCH, HTML_REPLACEMENT, SUBSTR, ATTR, VALUE, WHICH);
        assertEquals(-1, aStep1.compareTo(aStep2));
        assertEquals(1, aStep2.compareTo(aStep1));
    }
    public void test_Empty_Task() {
        String key = "TASK_01_02";
        String DIRECTORY = "./platform/samples";
        String OUTPUT = "./out/LuciadRIA/docs";
        String FILE_MASK = "samples_ria_geometry.html";
        boolean RECURSIVE = false;
        ArrayList<HTML_Step> steps = new ArrayList<HTML_Step>();
        HTML_Task html_target = new HTML_Task(key, DIRECTORY, OUTPUT, FILE_MASK, RECURSIVE, steps);
        assertEquals(0, html_target.STEPS.size());
    }

    public void test_Task_One_Step() {
        String NAME = "STEP_01";
        String ACTION = "REPLACE_WITH";
        String HTML_TARGET_MATCH = "nav#navbar";
        String HTML_REPLACEMENT = "./platform/samples/navbar.html";
        String SUBSTR = null;
        String ATTR = null;
        String VALUE = null;
        String WHICH = "FIRST";
        final HTML_Step aStep1 = new HTML_Step(NAME, ACTION, HTML_TARGET_MATCH, HTML_REPLACEMENT, SUBSTR, ATTR, VALUE, WHICH);

        String key = "TASK_01_02";
        String DIRECTORY = "./platform/samples";
        String OUTPUT = "./out/LuciadRIA/docs";
        String FILE_MASK = "samples_ria_geometry.html";
        boolean RECURSIVE = false;
        ArrayList<HTML_Step> steps = new ArrayList<HTML_Step>();
        steps.add(aStep1);
        HTML_Task html_target = new HTML_Task(key, DIRECTORY, OUTPUT, FILE_MASK, RECURSIVE, steps);
        assertEquals(1, html_target.STEPS.size());
    }

    public void test_ReadConfigurationSettings() {
        String configfile = "HTMLmanipulator.cfg";
        ReadConfigurationSettings readConfigurationSettings= new ReadConfigurationSettings(configfile);
        final JSONObject configurationSettings = readConfigurationSettings.ParseJason();
        Set<String> keySet = configurationSettings.keySet();
        assertEquals(3, keySet.size());
    }

}
