package org.example;

import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

//
// Decompiled by Procyon v0.5.36
//

public class ReadConfigurationSettings
{
    String configurationFile;

    public ReadConfigurationSettings(final String configurationFile) {
        this.configurationFile = configurationFile;
    }

    JSONObject ParseJason() {
        final JSONParser parser = new JSONParser();
        try {
            final Object obj = parser.parse(new FileReader(this.configurationFile));
            final JSONObject jsonObject = (JSONObject)obj;
            return jsonObject;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ParseException e2) {
            e2.printStackTrace();
        }
        return null;
    }
}
