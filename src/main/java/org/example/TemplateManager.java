package org.example;

import org.jsoup.select.Elements;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import org.jsoup.nodes.Document;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Entities;
import org.jsoup.Jsoup;
import java.io.File;
import java.util.*;

import org.json.simple.JSONObject;

import java.io.FileWriter;

//
// Decompiled by Procyon v0.5.36
//

public class TemplateManager
{
    String template;
    String outputFile;
    List<RKey> actions;
    FileWriter fw;

    public TemplateManager(final String aTemplate, final String anOutputFile) {
        this.actions = new ArrayList();
        this.template = aTemplate;
        this.outputFile = anOutputFile;
    }

    public void replaceHTMLcontentWith(final String key, final String html, final String aWhich) {
        final RKey keypair = new RKey(key, html);
        keypair.SetAction("REPLACE_WITH");
        keypair.SetWhich(aWhich);
        this.actions.add(keypair);
    }

    public void replaceHTMLappend(final String key, final String html, final String aWhich) {
        final RKey keypair = new RKey(key, html);
        keypair.SetAction("APPEND");
        keypair.SetWhich(aWhich);
        this.actions.add(keypair);
    }

    public void replaceHTMLprepend(final String key, final String html, final String aWhich) {
        final RKey keypair = new RKey(key, html);
        keypair.SetAction("PREPEND");
        keypair.SetWhich(aWhich);
        this.actions.add(keypair);
    }

    public void HTMLinsertBefore(final String key, final String html, final String aWhich) {
        final RKey keypair = new RKey(key, html);
        keypair.SetAction("INSERT_BEFORE");
        keypair.SetWhich(aWhich);
        this.actions.add(keypair);
    }

    public void HTMLinsertAfter(final String key, final String html, final String aWhich) {
        final RKey keypair = new RKey(key, html);
        keypair.SetAction("INSERT_AFTER");
        keypair.SetWhich(aWhich);
        this.actions.add(keypair);
    }

    public void replaceHTMLcontent(final String key, final String html, final String aWhich) {
        final RKey keypair = new RKey(key, html);
        keypair.SetAction("REPLACE");
        keypair.SetWhich(aWhich);
        this.actions.add(keypair);
    }

    public void deleteHTMLelement(final String key, final String aWhich) {
        final RKey keypair = new RKey(key, "");
        keypair.SetAction("DELETE");
        keypair.SetWhich(aWhich);
        this.actions.add(keypair);
    }

    public void unwrapHTMLelement(final String key, final String aWhich) {
        final RKey keypair = new RKey(key, "");
        keypair.SetAction("UNWRAP");
        keypair.SetWhich(aWhich);
        this.actions.add(keypair);
    }

    public void wrapHTMLelement(final String key, final String value, final String aWhich) {
        final RKey keypair = new RKey(key, "", value);
        keypair.SetAction("WRAP");
        keypair.SetWhich(aWhich);
        this.actions.add(keypair);
    }

    public void replaceAttr(final String key, final String attribute, final String value, final String aWhich) {
        final RKey keypair = new RKey(key, attribute, value);
        keypair.SetAction("ATTR_REPLACE");
        keypair.SetWhich(aWhich);
        this.actions.add(keypair);
    }

    public void replaceAttrSubstring(final String key, final String sub, final String attribute, final String value, final String aWhich) {
        final RKey keypair = new RKey(key, attribute, value);
        keypair.SetAction("ATTR_REPLACE_SUBSTR");
        keypair.SetWhich(aWhich);
        keypair.SetSubstring(sub);
        this.actions.add(keypair);
    }

    public void addClassToMatch(final String key, final String className, final String aWhich) {
        final RKey keypair = new RKey(key, className);
        keypair.SetAction("ADD_CLASS");
        keypair.SetWhich(aWhich);
        this.actions.add(keypair);
    }

    public void removeClassToMatch(final String key, final String className, final String aWhich) {
        final RKey keypair = new RKey(key, className);
        keypair.SetAction("REMOVE_CLASS");
        keypair.SetWhich(aWhich);
        this.actions.add(keypair);
    }

    public void replaceTextSubstring(final String key, final String sub, final String value, final String aWhich) {
        final RKey keypair = new RKey(key, "", value);
        keypair.SetAction("TEXT_REPLACE_SUBSTR");
        keypair.SetWhich(aWhich);
        keypair.SetSubstring(sub);
        this.actions.add(keypair);
    }

    public void replaceTextJSONMAP(final String key, final String json, final String attribute, final String which) {
        final ReadConfigurationSettings jsonFile = new ReadConfigurationSettings(json);
        final JSONObject jsonObject = jsonFile.ParseJason();
        for (final String sub : (Set<String>)  jsonObject.keySet()) {
            final String k = sub;
            final String value = (String) jsonObject.get(k);
            System.out.println(sub + " : " + value);
            this.replaceAttrSubstring(key, sub, attribute, value, which);
        }
    }

    public void replaceText(final String key, final String value, final String aWhich) {
        final RKey keypair = new RKey(key, "", value);
        keypair.SetAction("TEXT_REPLACE");
        keypair.SetWhich(aWhich);
        this.actions.add(keypair);
    }

    private void BuildFromTemplate() throws IOException {
        final File input = new File(this.template);
        final Document doc = Jsoup.parse(input, "UTF-8", "");
        final Document.OutputSettings settings = doc.outputSettings();
        settings.prettyPrint(false);
        settings.escapeMode(Entities.EscapeMode.extended);
        settings.charset("ASCII");
        System.out.println("     Executing actions: ");
        for (final RKey key : this.actions) {
            String preview = "";
            if (key.value.length() > 40) {
                preview = key.value.substring(0, 40) + "...";
            }
            else {
                preview = key.value;
            }
            preview = preview.split(System.getProperty("line.separator"))[0];
            System.out.println("\t- Action: " + key.action + " / Value: " + preview + " / Selector: " + key.key + " / Position: " + key.which);
            key.PerformAction(doc);
        }
        final File output = new File(this.outputFile);
        FileUtils.writeStringToFile(output, doc.outerHtml(), "UTF-8");
        final Path path = Paths.get(this.outputFile, new String[0]);
        final Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll("&amp;", "&");
        Files.write(path, content.getBytes(charset), new OpenOption[0]);
    }

    public void Close() throws IOException {
        this.fw = new FileWriter(this.outputFile);
        this.BuildFromTemplate();
        this.fw.close();
    }

    public class RKey
    {
        String key;
        String attribute;
        String value;
        String action;
        String which;
        String substr;

        public RKey(final String aKey, final String aHtml) {
            this.which = "ALL";
            this.key = aKey;
            this.attribute = "";
            this.value = aHtml;
        }

        public RKey(final String aKey, final String attrib, final String aValue) {
            this.which = "ALL";
            this.key = aKey;
            this.attribute = attrib;
            this.value = aValue;
        }

        public void SetAction(final String anAction) {
            this.action = anAction;
        }

        public void SetWhich(final String awhich) {
            this.which = awhich;
        }

        public void SetSubstring(final String asubstr) {
            this.substr = asubstr;
        }

        private String MatchMessage() {
            final String s = " [" + this.key + "] ->[" + this.value.substring(0, Math.min(this.value.length(), 20)) + "...";
            return s;
        }

        public void PerformAction(final Document doc) {
            if (this.action.equals("INSERT_BEFORE")) {
                final Elements tags_to_replace = doc.select(this.key);
                final Document doc_tmp = Jsoup.parse(this.value);
                final Element tag = doc_tmp.body().child(0);
                if (tags_to_replace.size() == 0) {
                    System.out.println("          - Elements with HTML insert before: 0 [No matches found]");
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements with HTML insert before: " + tags_to_replace.size());
                    for (final Element element : tags_to_replace) {
                        element.before(tag);
                    }
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements with HTML insert before: 1");
                    tags_to_replace.first().before(tag);
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements with HTML insert before: 1");
                    tags_to_replace.last().before(tag);
                }
            }
            else if (this.action.equals("INSERT_AFTER")) {
                final Elements tags_to_replace = doc.select(this.key);
                final Document doc_tmp = Jsoup.parse(this.value);
                final Element tag = doc_tmp.body().child(0);
                if (tags_to_replace.size() == 0) {
                    System.out.println("          - Elements with HTML insert after: 0 [No matches found]");
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements with HTML insert after: " + tags_to_replace.size());
                    for (final Element element : tags_to_replace) {
                        element.after(tag);
                    }
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements with HTML insert after: 1");
                    tags_to_replace.first().after(tag);
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements with HTML insert after: 1");
                    tags_to_replace.last().after(tag);
                }
            }
            else if (this.action.equals("REPLACE_WITH")) {
                final Elements tags_to_replace = doc.select(this.key);
                final Document doc_tmp = Jsoup.parse(this.value);
                final Element tag = doc_tmp.body().child(0);
                if (tags_to_replace.size() == 0) {
                    System.out.println("          - Elements with outer HTML replaced: 0 [No matches found]");
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements  with outer HTML replaced: " + tags_to_replace.size());
                    for (final Element element : tags_to_replace) {
                        element.replaceWith(tag);
                    }
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements with outer HTML replaced: 1");
                    tags_to_replace.first().replaceWith(tag);
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements with outer HTML replaced: 1");
                    tags_to_replace.last().replaceWith(tag);
                }
            }
            else if (this.action.equals("REPLACE")) {
                final Elements tags_to_replace = doc.select(this.key);
                if (tags_to_replace.size() == 0) {
                    System.out.println("          - Elements replaced: 0 [No matches found]");
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements replaced: " + tags_to_replace.size());
                    for (final Element element2 : tags_to_replace) {
                        element2.html(this.value);
                    }
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements replaced: 1");
                    tags_to_replace.first().html(this.value);
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements replaced: 1");
                    tags_to_replace.last().html(this.value);
                }
            }
            else if (this.action.equals("DELETE")) {
                final Elements tags_to_delete = doc.select(this.key);
                if (tags_to_delete.size() == 0) {
                    System.out.println("          - Elements deleted: 0 [No matches found]");
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements deleted: " + tags_to_delete.size());
                    tags_to_delete.remove();
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements deleted: 1");
                    tags_to_delete.first().remove();
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements deleted: 1");
                    tags_to_delete.last().remove();
                }
            }
            else if (this.action.equals("UNWRAP")) {
                final Elements tags_to_unwrap = doc.select(this.key);
                if (tags_to_unwrap.size() == 0) {
                    System.out.println("          - Elements unwrapped: 0 [No matches found]");
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements unwrapped: " + tags_to_unwrap.size());
                    tags_to_unwrap.unwrap();
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements unwrapped: 1");
                    tags_to_unwrap.first().unwrap();
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements unwrapped: 1");
                    tags_to_unwrap.last().unwrap();
                }
            }
            else if (this.action.equals("WRAP")) {
                final Elements tags_to_wrap = doc.select(this.key);
                if (tags_to_wrap.size() == 0) {
                    System.out.println("          - Elements wrapped: 0 [No matches found]");
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements wrapped: " + tags_to_wrap.size());
                    tags_to_wrap.wrap(this.value);
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements wrapped: 1");
                    tags_to_wrap.first().wrap(this.value);
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements wrapped: 1");
                    tags_to_wrap.last().wrap(this.value);
                }
            }
            else if (this.action.equals("ATTR_REPLACE")) {
                final Elements values_to_replace = doc.select(this.key);
                if (values_to_replace.size() == 0) {
                    System.out.println("          - Elements with replaced values: 0 [No matches found]");
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements with replaced values: " + values_to_replace.size());
                    for (final Element element2 : values_to_replace) {
                        element2.attr(this.attribute, this.value);
                    }
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements with replaced values: 1");
                    values_to_replace.first().attr(this.attribute, this.value);
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements with replaced values: 1");
                    values_to_replace.last().attr(this.attribute, this.value);
                }
            }
            else if (this.action.equals("ADD_CLASS")) {
                final Elements listofMatches = doc.select(this.key);
                if (listofMatches.size() == 0) {
                    System.out.println("          - Elements with class added [No matches found]");
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements with class added: " + listofMatches.size());
                    for (final Element element2 : listofMatches) {
                        element2.addClass(this.value);
                    }
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements with class added : 1");
                    final Element element3 = listofMatches.first();
                    element3.addClass(this.value);
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements with class added: 1");
                    final Element element3 = listofMatches.last();
                    element3.addClass(this.value);
                }
            }
            else if (this.action.equals("REMOVE_CLASS")) {
                final Elements listofMatches = doc.select(this.key);
                if (listofMatches.size() == 0) {
                    System.out.println("          - Elements with class removed [No matches found]");
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements with class removed: " + listofMatches.size());
                    for (final Element element2 : listofMatches) {
                        element2.removeClass(this.value);
                    }
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements with class removed : 1");
                    final Element element3 = listofMatches.first();
                    element3.removeClass(this.value);
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements with class removed: 1");
                    final Element element3 = listofMatches.last();
                    element3.removeClass(this.value);
                }
            }
            else if (this.action.equals("ATTR_REPLACE_SUBSTR")) {
                final Elements values_to_replace = doc.select(this.key);
                if (values_to_replace.size() == 0) {
                    System.out.println("          - Elements with attributes with replaced substrings: 0 [No matches found]");
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements with attributes with replaced substrings: " + values_to_replace.size());
                    for (final Element element2 : values_to_replace) {
                        final String original_value = element2.attr(this.attribute);
                        final String new_value = original_value.replaceAll(this.substr, this.value);
                        element2.attr(this.attribute, new_value);
                    }
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements with attributes with replaced substrings: 1");
                    final Element element3 = values_to_replace.first();
                    final String original_value2 = element3.attr(this.attribute);
                    final String new_value2 = original_value2.replaceAll(this.substr, this.value);
                    element3.attr(this.attribute, new_value2);
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements with attributes with replaced substrings: 1");
                    final Element element3 = values_to_replace.last();
                    final String original_value2 = element3.attr(this.attribute);
                    final String new_value2 = original_value2.replaceAll(this.substr, this.value);
                    element3.attr(this.attribute, new_value2);
                }
            }
            else if (this.action.equals("TEXT_REPLACE")) {
                final Elements values_to_replace = doc.select(this.key);
                if (values_to_replace.size() == 0) {
                    System.out.println("          - Elements with replaced text: 0 [No matches found]");
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements with replaced text: " + values_to_replace.size());
                    for (final Element element2 : values_to_replace) {
                        element2.text(this.value);
                    }
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements with replaced text: 1");
                    values_to_replace.first().text(this.value);
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements with replaced text: 1");
                    values_to_replace.last().text(this.value);
                }
            }
            else if (this.action.equals("TEXT_REPLACE_SUBSTR")) {
                final Elements values_to_replace = doc.select(this.key);
                if (values_to_replace.size() == 0) {
                    System.out.println("          - Elements with replaced text with replaced substrings: (" + this.substr + ")  [No matches found]");
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements with replaced text with replaced substrings: (" + this.substr + ") " + values_to_replace.size());
                    for (final Element element2 : values_to_replace) {
                        final String original_value = element2.text();
                        final String new_value = original_value.replaceAll(this.substr, this.value);
                        element2.text(new_value);
                    }
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements with replaced text with replaced substrings: (" + this.substr + ") " + 1);
                    final Element element3 = values_to_replace.first();
                    final String original_value2 = element3.text();
                    final String new_value2 = original_value2.replaceAll(this.substr, this.value);
                    element3.text(new_value2);
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements with replaced text with replaced substrings: (" + this.substr + ") " + 1);
                    final Element element3 = values_to_replace.last();
                    final String original_value2 = element3.text();
                    final String new_value2 = original_value2.replaceAll(this.substr, this.value);
                    element3.text(new_value2);
                }
            }
            else if (this.action.equals("APPEND")) {
                final Elements tags_to_replace = doc.select(this.key);
                if (tags_to_replace.size() == 0) {
                    System.out.println("          - Elements with appended HTML: 0 [No matches found]" + this.MatchMessage());
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements with appended HTML: " + tags_to_replace.size() + this.MatchMessage());
                    for (final Element element2 : tags_to_replace) {
                        element2.append(this.value);
                    }
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements with appended HTML: 1" + this.MatchMessage());
                    tags_to_replace.first().append(this.value);
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements with appended HTML: 1" + this.MatchMessage());
                    tags_to_replace.last().append(this.value);
                }
            }
            else if (this.action.equals("PREPEND")) {
                final Elements tags_to_replace = doc.select(this.key);
                if (tags_to_replace.size() == 0) {
                    System.out.println("          - Elements with prepended HTML: 0 [No matches found]");
                    return;
                }
                if (this.which.equals("ALL")) {
                    System.out.println("          - Elements with prepended HTML: " + tags_to_replace.size());
                    for (final Element element2 : tags_to_replace) {
                        element2.prepend(this.value);
                    }
                }
                else if (this.which.equals("FIRST")) {
                    System.out.println("          - Elements with prepended HTML: 1");
                    tags_to_replace.first().prepend(this.value);
                }
                else if (this.which.equals("LAST")) {
                    System.out.println("          - Elements with prepended HTML: 1");
                    tags_to_replace.last().prepend(this.value);
                }
            }
        }
    }
}
