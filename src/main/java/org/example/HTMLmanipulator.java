package org.example;

import java.util.*;
import java.util.logging.Formatter;
import java.util.logging.SimpleFormatter;
import java.util.logging.Handler;
import java.util.logging.FileHandler;

import org.json.simple.JSONObject;
import java.util.logging.Logger;

//
// Decompiled by Procyon v0.5.36
//

public class HTMLmanipulator
{
    ArrayList<HTML_Task> TASKS;
    public static Logger logger;
    JSONObject configurationSettings;

    public HTMLmanipulator() {
        this.TASKS = new ArrayList<HTML_Task>();
    }

    public void done() {
    }

    boolean ReadConfig(final String configFile) {
        final ReadConfigurationSettings readConfigurationSettings = new ReadConfigurationSettings(configFile);
        final JSONObject configurationSettings = readConfigurationSettings.ParseJason();
        for (final String key : (Set<String>) configurationSettings.keySet()) {
            final JSONObject task = (JSONObject) configurationSettings.get(key);
            final String DIRECTORY = (String) task.get("DIRECTORY");
            final String FILE_MASK = (String) task.get("FILE_MASK");
            final Boolean RECURSIVE_TEST = (Boolean) task.get("RECURSIVE");
            final boolean RECURSIVE = RECURSIVE_TEST != null && RECURSIVE_TEST;
            String OUTPUT = (String) task.get("OUTPUT");
            if (OUTPUT == null) {
                OUTPUT = "out";
            }
            final ArrayList<HTML_Step> steps = new ArrayList<HTML_Step>();
            for (final String element : (Set<String>) task.keySet()) {
                if (element.startsWith("STEP_")) {
                    final JSONObject step = (JSONObject) task.get(element);
                    if (step == null) {
                        continue;
                    }
                    final String ACTION = (String) step.get("ACTION");
                    final String HTML_TARGET_MATCH = (String) step.get("HTML_TARGET_MATCH");
                    final String HTML_REPLACEMENT = (String) step.get("HTML_REPLACEMENT");
                    final String ATTR = (String) step.get("ATTR");
                    final String VALUE = (String) step.get("VALUE");
                    final String WHICH = (String) step.get("WHICH");
                    final String SUBSTR = (String) step.get("SUBSTR");
                    final HTML_Step aStep = new HTML_Step(element, ACTION, HTML_TARGET_MATCH, HTML_REPLACEMENT, SUBSTR, ATTR, VALUE, WHICH);
                    steps.add(aStep);
                }
            }
            final HTML_Task html_target = new HTML_Task(key, DIRECTORY, OUTPUT, FILE_MASK, RECURSIVE, steps);
            this.TASKS.add(html_target);
        }
        return true;
    }

    public void run(final String configfile) throws Exception {
        final FileHandler fh = new FileHandler("HTMLmanipulator.log");
        HTMLmanipulator.logger.addHandler(fh);
        final SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        if (this.ReadConfig(configfile)) {
            Collections.sort(this.TASKS);
            for (final HTML_Task target : this.TASKS) {
                target.PerformAction();
            }
        }
        else {
            HTMLmanipulator.logger.severe("Please check your settings at the ConfluenceCopier.cfg file ");
        }
    }

    static {
        HTMLmanipulator.logger = Logger.getLogger("HTMLmanipulatorLog");
    }
}
